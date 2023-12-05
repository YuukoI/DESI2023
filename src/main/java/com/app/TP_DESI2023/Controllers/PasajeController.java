package com.app.TP_DESI2023.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TP_DESI2023.Entitys.Cliente;
import com.app.TP_DESI2023.Entitys.Vuelo;
import com.app.TP_DESI2023.Exceptions.CustomException;
import com.app.TP_DESI2023.Services.AsientoService;
import com.app.TP_DESI2023.Services.ClienteService;
import com.app.TP_DESI2023.Services.ImpuestoService;
import com.app.TP_DESI2023.Services.PasajeService;
import com.app.TP_DESI2023.Services.VueloService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PasajeController {
	
	@Autowired
	private PasajeService pasajeService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private VueloService vueloService;
	
	@Autowired
	private AsientoService asientoService;
	
	@Autowired
	private ImpuestoService impuestoService; 
	
    @GetMapping("/validacion_cliente")
    public String mostrarPaginaValidacionCliente() {
        return "validacion_cliente";
    }

    @PostMapping("/validacion_cliente")
    public String validarCliente(@RequestParam("dni") int dni, HttpSession session) throws CustomException {
        Cliente c = clienteService.obtenerClientePorDni(dni);

        if (c != null) {
            session.setAttribute("dni", dni);
            return "redirect:/pasajes";
        }

        throw new CustomException("No existe el cliente.");
    }
    
    
    @GetMapping("/pasajes")
    public String mostrarPaginaPasajes(HttpSession session, Model model) {
        Integer dni = (Integer) session.getAttribute("dni");
        Cliente c = clienteService.obtenerClientePorDni(dni);
         
        model.addAttribute("cliente", c);
        model.addAttribute("vuelos", vueloService.obtenerVuelos());

        return "pasajes";
    }
    
    
    @PostMapping("/pasajes")
    public String procesarPasajes(HttpSession session, Model model) {
        
      
        Integer dni = (Integer) session.getAttribute("dni");
        
        String nroVuelo = (String) session.getAttribute("nroVuelo");
        Optional<Vuelo> vueloOptional = vueloService.obtenerVueloPorNro(nroVuelo);
        Cliente c = clienteService.obtenerClientePorDni(dni);
        
        model.addAttribute("vueloSeleccionado", vueloOptional);
        model.addAttribute("dni", c.getDni());

        if (vueloOptional.isPresent()) {
            if (vueloOptional.get().getTipoVuelo().equalsIgnoreCase("NACIONAL")) {
                return "redirect:/venta_pasaje_nacional";
            } else {
                return "redirect:/venta_pasaje_internacional";
            }
        }

        return "redirect:/pagina_de_error";
    }
    
    @GetMapping("/venta_pasaje_nacional")
    public String MostrarventaPasajesNacionales(HttpSession session, Model model) {
    	Integer dni = (Integer) session.getAttribute("dni");
    	String nroVuelo = (String) session.getAttribute("nroVuelo");

    	Optional<Vuelo> vueloOptional = vueloService.obtenerVueloPorNro(nroVuelo);
    	Cliente c = clienteService.obtenerClientePorDni(dni);

    	model.addAttribute("vueloSeleccionado", vueloOptional);
    	model.addAttribute("Cliente", c);

        return "venta_pasaje_nacional";
    }
    
    @PostMapping("/venta_pasaje_nacional")
    public String procesarVentaNacional(HttpSession session, Model model) {
        Integer dni = (Integer) session.getAttribute("dni");
        String nroVuelo = (String) session.getAttribute("nroVuelo");

        Cliente c = clienteService.obtenerClientePorDni(dni);
        Optional<Vuelo> vueloOptional = vueloService.obtenerVueloPorNro(nroVuelo);
       
        if (vueloOptional.isPresent()) {
            Vuelo vuelo = vueloOptional.get();
            model.addAttribute("Cliente", c);
            model.addAttribute("Vuelo", vuelo);
        }

        return "venta_pasaje_nacional";
    }
}

