package ar.edu.unju.fi.poo.service.imp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.poo.dto.CuotaResponseDTO;
import ar.edu.unju.fi.poo.dto.DetalleVentaRequestDTO;
import ar.edu.unju.fi.poo.dto.DetalleVentaResponseDTO;
import ar.edu.unju.fi.poo.dto.VentaContadoRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaFinanciadaRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.exceptions.ClienteNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.CuotaNoEncontradaException;
import ar.edu.unju.fi.poo.exceptions.CuponInvalidoException;
import ar.edu.unju.fi.poo.exceptions.ProductoNoEncontradoException;
import ar.edu.unju.fi.poo.exceptions.StockInsuficienteException;
import ar.edu.unju.fi.poo.exceptions.VentaFinanciadaInvalidaException;
import ar.edu.unju.fi.poo.exceptions.VentaInvalidaException;
import ar.edu.unju.fi.poo.exceptions.VentaYaSaldadaException;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.Cuota;
import ar.edu.unju.fi.poo.model.Cupon;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.EstadoCuota;
import ar.edu.unju.fi.poo.model.EstadoVenta;
import ar.edu.unju.fi.poo.model.Producto;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.model.VentaFinanciada;
import ar.edu.unju.fi.poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.repository.CuotaRepository;
import ar.edu.unju.fi.poo.repository.CuponRepository;
import ar.edu.unju.fi.poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.repository.VentaFinanciadaRepository;
import ar.edu.unju.fi.poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.service.VentaService;
import ar.edu.unju.fi.poo.dto.ClienteDto;
import ar.edu.unju.fi.poo.dto.CuotaEstadoCuentaDTO;
import ar.edu.unju.fi.poo.dto.EstadoCuentaResponseDTO;
import ar.edu.unju.fi.poo.dto.VentaEstadoCuentaDTO;

@Service
public class VentaServiceImp implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final CuponRepository cuponRepository;
    private final VentaFinanciadaRepository ventaFinanciadaRepository;
    private final CuotaRepository cuotaRepository;

    public VentaServiceImp(VentaRepository ventaRepository, ProductoRepository productoRepository,
            ClienteRepository clienteRepository, CuponRepository cuponRepository,
            VentaFinanciadaRepository ventaFinanciadaRepository, CuotaRepository cuotaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.cuponRepository = cuponRepository;
        this.ventaFinanciadaRepository = ventaFinanciadaRepository;
        this.cuotaRepository = cuotaRepository;
    }

    // ------------------ VENTA CONTADO --------------------------

    @Override
    @Transactional
    public VentaResponseDTO registrarVentaContado(VentaContadoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new VentaInvalidaException("La solicitud de venta no puede ser nula.");
        }

        VentaContado venta = new VentaContado();
        venta.setCodigoComprobante("COMP-" + System.currentTimeMillis());
        venta.setFechaVenta(LocalDate.now());
        venta.setCliente(obtenerCliente(requestDTO.getIdCliente()));

        if (requestDTO.getCodigoCupon() != null && !requestDTO.getCodigoCupon().isBlank()) {
            Cupon cupon = cuponRepository.buscarPorCodigo(requestDTO.getCodigoCupon())
                    .orElseThrow(() -> new CuponInvalidoException("El cupon no existe."));
            if (!cupon.esValido()) {
                throw new CuponInvalidoException("El cupon esta vencido o ya fue utilizado.");
            }
            venta.setCupon(cupon);
        }

        cargarDetallesYValidarStock(venta, requestDTO.getDetalles());
        venta.calcularTotal();
        actualizarStock(venta);

        if (venta.getCupon() != null) {
            venta.getCupon().setUsado(true);
            cuponRepository.save(venta.getCupon());
        }

        VentaContado ventaGuardada = ventaRepository.save(venta);
        return convertirAVentaDTO(ventaGuardada);
    }
    
    // ------------------ COBRO DE CUOTAS --------------------------

    @Override
    @Transactional
    public CuotaResponseDTO cobrarCuota(Integer idVenta) {
        if (idVenta == null) {
            throw new VentaFinanciadaInvalidaException("El identificador de la venta no puede ser nulo.");
        }

        VentaFinanciada venta = ventaFinanciadaRepository.findById(idVenta)
                .orElseThrow(() -> new VentaFinanciadaInvalidaException("No se encontró una venta financiada con ID: " + idVenta));

        if (venta.getEstado() == EstadoVenta.SALDADA) {
            throw new VentaYaSaldadaException("La venta con ID " + idVenta + " ya se encuentra saldada.");
        }

        List<Cuota> cuotas = cuotaRepository.findByVentaOrdenadas(idVenta);
        if (cuotas == null || cuotas.isEmpty()) {
            throw new CuotaNoEncontradaException("La venta no posee cuotas registradas.");
        }

        Cuota cuotaACobrar = null;
        for (Cuota c : cuotas) {
            c.actualizarEstadoPorVencimiento();
            if (c.getEstado() != EstadoCuota.PAGADA) {
                cuotaACobrar = c;
                break; // Toma estrictamente la primera impaga en orden ascendente (no saltea cuotas)
            }
        }

        if (cuotaACobrar == null) {
            venta.marcarComoSaldada();
            ventaFinanciadaRepository.save(venta);
            throw new VentaYaSaldadaException("Todas las cuotas de la venta ya están pagadas.");
        }

        cuotaACobrar.marcarPagoCuota();
        Cuota cuotaPagada = cuotaRepository.save(cuotaACobrar);

        boolean quedanPendientes = false;
        for (Cuota c : cuotas) {
            if (c.getEstado() != EstadoCuota.PAGADA) {
                quedanPendientes = true;
                break;
            }
        }

        if (!quedanPendientes) {
            venta.marcarComoSaldada();
            ventaFinanciadaRepository.save(venta);
        }

        CuotaResponseDTO dto = new CuotaResponseDTO();
        dto.setIdCuota(cuotaPagada.getIdCuota());
        dto.setNumeroCuota(cuotaPagada.getNumeroCuota());
        dto.setImporte(cuotaPagada.getImporte());
        dto.setFechaVencimiento(cuotaPagada.getFechaVencimiento());
        dto.setEstado(cuotaPagada.getEstado().name());
        return dto;
    }


    // ------------------ VENTA FINANCIADA --------------------------

    @Override
    @Transactional
    public VentaResponseDTO registrarVentaFinanciada(VentaFinanciadaRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new VentaFinanciadaInvalidaException("La solicitud de venta financiada no puede ser nula.");
        }
        if (requestDTO.getDetalles() == null || requestDTO.getDetalles().isEmpty()) {
            throw new VentaFinanciadaInvalidaException("La venta debe tener al menos un detalle.");
        }
        if (requestDTO.getPorcentajeRecargo() != null && requestDTO.getPorcentajeRecargo() < 0) {
            throw new VentaFinanciadaInvalidaException("El recargo financiero no puede ser negativo.");
        }

        VentaFinanciada venta = new VentaFinanciada();
        venta.setCodigoComprobante("VF-" + System.currentTimeMillis());
        venta.setFechaVenta(LocalDate.now());
        venta.setPorcentajeRecargo(requestDTO.getPorcentajeRecargo() != null ? requestDTO.getPorcentajeRecargo() : 0.0);
        venta.setCliente(obtenerCliente(requestDTO.getIdCliente()));

        cargarDetallesYValidarStock(venta, requestDTO.getDetalles());
        venta.calcularTotal();
        venta.generarCuotas();
        actualizarStock(venta);

        VentaFinanciada ventaGuardada = ventaFinanciadaRepository.save(venta);
        return convertirAVentaDTO(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuotaResponseDTO> consultarEstadoCuentaCuotasPendientes(Integer idCliente) {
        if (idCliente == null) {
            throw new VentaFinanciadaInvalidaException("El identificador del cliente no puede ser nulo.");
        }
        if (!clienteRepository.existsById(idCliente)) {
            throw new ClienteNoEncontradoException("No existe el cliente con ID: " + idCliente);
        }
        List<Cuota> cuotas = cuotaRepository.findByClienteIdAndEstado(idCliente, EstadoCuota.ADEUDADA);
        return convertirAListaCuotasDTO(cuotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuotaResponseDTO> consultarEstadoCuentaCuotasPendientesPorIdentificacion(String numeroIdentificacion) {
        if (numeroIdentificacion == null || numeroIdentificacion.isBlank()) {
            throw new VentaFinanciadaInvalidaException("El numero de identificación del cliente es obligatorio.");
        }
        List<Cuota> cuotas = cuotaRepository.findByClienteNumeroIdentificacionAndEstado(numeroIdentificacion, EstadoCuota.ADEUDADA);
        return convertirAListaCuotasDTO(cuotas);
    }

    // ------------------ CONSULTAS GENERALES DE VENTAS --------------------------

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarHistorialPorCliente(Integer idCliente) {
        return convertirAListaVentaDTO(ventaRepository.findByCliente_IdCliente(idCliente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null || fechaHasta == null || fechaDesde.isAfter(fechaHasta)) {
            throw new VentaInvalidaException("El rango de fechas no es valido.");
        }
        return convertirAListaVentaDTO(ventaRepository.findByFechaVentaBetween(fechaDesde, fechaHasta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarPorModalidad(String modalidad) {
        if (modalidad == null || modalidad.isBlank()) {
            throw new VentaInvalidaException("Debe indicar una modalidad de venta.");
        }
        if (modalidad.equalsIgnoreCase("CONTADO")) {
            return convertirAListaVentaDTO(ventaRepository.findVentasContado());
        } else if (modalidad.equalsIgnoreCase("FINANCIADO") || modalidad.equalsIgnoreCase("FINANCIADA")) {
            return convertirAListaVentaDTO(ventaRepository.findVentasFinanciadas());
        }
        throw new VentaInvalidaException("La modalidad indicada no es válida.");
    }

    // ------------------ METODOS AUXILIARES DE NEGOCIO --------------------------

    private Cliente obtenerCliente(Integer idCliente) {
        if (idCliente == null) {
            throw new VentaInvalidaException("El cliente es obligatorio.");
        }
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("No existe el cliente con ID: " + idCliente));
    }

    private void cargarDetallesYValidarStock(Venta venta, List<DetalleVentaRequestDTO> detallesDto) {
        if (detallesDto == null || detallesDto.isEmpty()) {
            throw new VentaInvalidaException("La venta debe tener al menos un detalle.");
        }

        for (DetalleVentaRequestDTO detDto : detallesDto) {
            if (detDto == null || detDto.getIdProducto() == null) {
                throw new VentaInvalidaException("El detalle y el producto son obligatorios.");
            }
            if (detDto.getCantidad() == null || detDto.getCantidad() <= 0) {
                throw new VentaInvalidaException("La cantidad debe ser mayor que cero.");
            }

            Integer idProducto = detDto.getIdProducto();
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new ProductoNoEncontradoException("No existe el producto con ID: " + idProducto));

            if (producto.getStock() < detDto.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioBaseAplicado(producto.getPrecioBase());
            detalle.calcularSubtotal();

            venta.getDetalles().add(detalle);
        }
    }

    private void actualizarStock(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }
    }

    // ------------------ CONVERSORES A DTO --------------------------

    private VentaResponseDTO convertirAVentaDTO(Venta venta) {
        if (venta == null) return null;

        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFecha(venta.getFechaVenta());
        dto.setModalidad(venta instanceof VentaContado ? "CONTADO" : "FINANCIADA");

        if (venta.getCliente() != null) {
            dto.setIdCliente(venta.getCliente().getIdCliente());
            String nombre = venta.getCliente().getNombre() != null ? venta.getCliente().getNombre() : "";
            String apellido = venta.getCliente().getApellido() != null ? venta.getCliente().getApellido() : "";
            String nombreCompleto = (nombre + " " + apellido).trim();
            dto.setNombreCliente(!nombreCompleto.isEmpty() ? nombreCompleto : null);
        }

        List<DetalleVentaResponseDTO> detallesDTO = new ArrayList<>();
        double sumaSubtotales = 0.0;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                DetalleVentaResponseDTO detalleDTO = convertirADetalleDTO(detalle);
                if (detalleDTO != null) {
                    detallesDTO.add(detalleDTO);
                    if (detalleDTO.getSubtotal() != null) {
                        sumaSubtotales += detalleDTO.getSubtotal();
                    }
                }
            }
        }
        dto.setDetalles(detallesDTO);
        dto.setSutotal(sumaSubtotales);
        dto.setTotal(venta.getTotal() > 0 ? venta.getTotal() : sumaSubtotales);
        return dto;
    }

    private DetalleVentaResponseDTO convertirADetalleDTO(DetalleVenta detalle) {
        if (detalle == null) return null;

        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
        dto.setIdDetalleVenta(detalle.getIdDetalle());
        dto.setCantidad(detalle.getCantidad());

        double precioUnitario = 0.0;
        if (detalle.getProducto() != null) {
            dto.setIdProducto(detalle.getProducto().getIdProducto());
            dto.setNombreProducto(detalle.getProducto().getNombre());
            if (detalle.getProducto().getPrecioBase() != null) {
                precioUnitario = detalle.getProducto().getPrecioBase();
            }
            dto.setPrecioUnitario(precioUnitario);
        }

        if (detalle.getSubtotal() != null && detalle.getSubtotal() > 0.0) {
            dto.setSubtotal(detalle.getSubtotal());
        } else {
            dto.setSubtotal(precioUnitario * (detalle.getCantidad() != null ? detalle.getCantidad() : 0));
        }

        return dto;
    }

    private List<VentaResponseDTO> convertirAListaVentaDTO(List<Venta> ventas) {
        List<VentaResponseDTO> dtos = new ArrayList<>();
        if (ventas != null) {
            for (Venta venta : ventas) {
                dtos.add(convertirAVentaDTO(venta));
            }
        }
        return dtos;
    }

    private List<CuotaResponseDTO> convertirAListaCuotasDTO(List<Cuota> cuotas) {
        List<CuotaResponseDTO> dtos = new ArrayList<>();
        if (cuotas != null) {
            for (Cuota cuota : cuotas) {
                CuotaResponseDTO dto = new CuotaResponseDTO();
                dto.setIdCuota(cuota.getIdCuota());
                dto.setNumeroCuota(cuota.getNumeroCuota());
                dto.setImporte(cuota.getImporte());
                dto.setFechaVencimiento(cuota.getFechaVencimiento());
                dto.setEstado(cuota.getEstado() != null ? cuota.getEstado().name() : null);
                dtos.add(dto);
            }
        }
        return dtos;
    }
 // ------------------ ESTADO DE CUENTA CLIENTE --------------------------

    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponseDTO consultarEstadoCuentaCliente(Integer idCliente) {
        if (idCliente == null) {
            throw new ClienteNoEncontradoException("El identificador del cliente no puede ser nulo.");
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró el cliente con ID: " + idCliente));

        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setIdCliente(cliente.getIdCliente());
        clienteDto.setNumeroIdentificacion(cliente.getNumeroIdentificacion());
        clienteDto.setNombre(cliente.getNombre());
        clienteDto.setApellido(cliente.getApellido());
        clienteDto.setRazonSocial(cliente.getRazonSocial());
        clienteDto.setTelefono(cliente.getTelefono());
        clienteDto.setEmail(cliente.getEmail());
        clienteDto.setDireccion(cliente.getDireccion());
        clienteDto.setTipoCliente(cliente.getTipoCliente());

        List<Venta> ventasCliente = ventaRepository.findByCliente_IdCliente(idCliente);
        List<VentaEstadoCuentaDTO> ventasDTO = new ArrayList<>();

        for (Venta venta : ventasCliente) {
            VentaEstadoCuentaDTO ventaDto = new VentaEstadoCuentaDTO();
            ventaDto.setIdVenta(venta.getIdVenta());
            ventaDto.setCodigoComprobante(venta.getCodigoComprobante());
            ventaDto.setFechaVenta(venta.getFechaVenta());
            ventaDto.setTotal(venta.getTotal());

            if (venta instanceof VentaFinanciada) {
                ventaDto.setModalidad("FINANCIADA");
                List<Cuota> cuotas = cuotaRepository.findByVentaOrdenadas(venta.getIdVenta());
                List<CuotaEstadoCuentaDTO> cuotasDTO = new ArrayList<>();

                for (Cuota cuota : cuotas) {
                    cuota.actualizarEstadoPorVencimiento();
                    CuotaEstadoCuentaDTO cuotaDto = new CuotaEstadoCuentaDTO();
                    cuotaDto.setIdCuota(cuota.getIdCuota());
                    cuotaDto.setNumeroCuota(cuota.getNumeroCuota());
                    cuotaDto.setImporte(cuota.getImporte());
                    cuotaDto.setFechaVencimiento(cuota.getFechaVencimiento());
                    cuotaDto.setEstado(cuota.getEstado());
                    cuotasDTO.add(cuotaDto);
                }
                ventaDto.setCuotas(cuotasDTO);
            } else {
                ventaDto.setModalidad("CONTADO");
            }

            ventasDTO.add(ventaDto);
        }

        EstadoCuentaResponseDTO response = new EstadoCuentaResponseDTO();
        response.setCliente(clienteDto);
        response.setVentas(ventasDTO);
        return response;
    }
}