package ar.edu.unju.fi.poo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unju.fi.poo.dto.DetalleVentaResponseDTO;
import ar.edu.unju.fi.poo.dto.VentaContadoRequestDTO;
import ar.edu.unju.fi.poo.dto.VentaResponseDTO;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.DetalleVenta;
import ar.edu.unju.fi.poo.model.Venta;
import ar.edu.unju.fi.poo.model.VentaContado;
import ar.edu.unju.fi.poo.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // 1. POST (Registro de venta al contado)
    @PostMapping("/contado")
    public ResponseEntity<VentaResponseDTO> registrarVentaContado(@RequestBody VentaContadoRequestDTO requestDTO) {
        VentaContado ventaProcesada = ventaService.registrarVentaContado(convertirARequestEntity(requestDTO));
        return new ResponseEntity<>(convertirAVentaDTO(ventaProcesada), HttpStatus.CREATED);
    }

    // 2. GET (Historial de ventas de un cliente)
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerHistorialCliente(@PathVariable Integer idCliente) {
        List<Venta> ventas = ventaService.buscarHistorialPorCliente(idCliente);
        return ResponseEntity.ok(convertirAListaVentaDTO(ventas));
    }

    // 3. GET (Consultar por rango de fechas)
    @GetMapping("/fechas")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorRangoFechas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<Venta> ventas = ventaService.buscarPorRangoFechas(desde, hasta);
        return ResponseEntity.ok(convertirAListaVentaDTO(ventas));
    }

    // 4. GET (Filtrar por modalidad: CONTADO o FINANCIADA)
    @GetMapping("/modalidad/{modalidad}")
    public ResponseEntity<List<VentaResponseDTO>> buscarPorModalidad(@PathVariable String modalidad) {
        List<Venta> ventas = ventaService.buscarPorModalidad(modalidad);
        return ResponseEntity.ok(convertirAListaVentaDTO(ventas));
    }

    // MÉTODOS AUXILIARES DE CONVERSIÓN (MAPPING)
    private VentaContado convertirARequestEntity(VentaContadoRequestDTO dto) {
        if (dto == null) return null;
        
        VentaContado venta = new VentaContado();
        
        venta.setCodigoComprobante("COMP-" + System.currentTimeMillis());
        venta.setFechaVenta(LocalDate.now()); 
        
        if (dto.getIdCliente() != null) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(dto.getIdCliente().intValue());
            
            List<Venta> ventasPrevias = ventaService.buscarHistorialPorCliente(dto.getIdCliente().intValue());
            if (ventasPrevias != null && !ventasPrevias.isEmpty()) {
                Cliente clienteGuardado = ventasPrevias.get(0).getCliente();
                if (clienteGuardado != null) {
                    cliente = clienteGuardado;
                }
            }
            venta.setCliente(cliente);
        }

        if (dto.getDetalles() != null) {
            List<DetalleVenta> detalles = new ArrayList<>();
            for (var detDto : dto.getDetalles()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                
                if (detDto.getIdProducto() != null) {
                    ar.edu.unju.fi.poo.model.Producto producto = new ar.edu.unju.fi.poo.model.Producto();
                    producto.setIdProducto(detDto.getIdProducto().intValue());
                    producto.setPrecioBase(0.0);
                    detalle.setProducto(producto);
                }
                
                detalle.setCantidad(detDto.getCantidad());
                detalle.setPrecioBaseAplicado(0.0);
                
                detalles.add(detalle);
            }
            
            if (venta.getDetalles() != null) {
                venta.getDetalles().addAll(detalles);
            }
        }

        return venta;
    }

    private VentaResponseDTO convertirAVentaDTO(Venta venta) {
        if (venta == null) return null;

        VentaResponseDTO dto = new VentaResponseDTO();
        
        if (venta.getIdVenta() != null) {
            dto.setIdVenta(Long.valueOf(venta.getIdVenta()));
        }
        
        dto.setFecha(venta.getFechaVenta());
        
        if (venta instanceof VentaContado) {
            dto.setModalidad("CONTADO");
        } else {
            dto.setModalidad("FINANCIADA");
        }

        // Mapeo Cliente
        if (venta.getCliente() != null) {
            if (venta.getCliente().getIdCliente() != null) {
                dto.setIdCliente(Long.valueOf(venta.getCliente().getIdCliente()));
            }
            
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

        dto.setTotal(sumaSubtotales);

        return dto;
    }

    private DetalleVentaResponseDTO convertirADetalleDTO(DetalleVenta detalle) {
        if (detalle == null) return null;

        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
        if (detalle.getIdDetalle() != null) {
            dto.setIdDetalleVenta(Long.valueOf(detalle.getIdDetalle()));
        }
        
        dto.setCantidad(detalle.getCantidad());

        double precioUnitario = 0.0;
        if (detalle.getProducto() != null) {
            if (detalle.getProducto().getIdProducto() != null) {
                dto.setIdProducto(Long.valueOf(detalle.getProducto().getIdProducto()));
            }
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
}