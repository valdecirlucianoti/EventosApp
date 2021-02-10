package com.eventosapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventosapp.model.Convidado;
import com.eventosapp.model.Evento;
import com.eventosapp.report.EventoReport;
import com.eventosapp.repository.ConvidadoRepository;
import com.eventosapp.repository.EventoRepository;

@Controller
public class EventoController {
	@Autowired
	private EventoRepository er;

	@Autowired
	ConvidadoRepository cr;
	
	@Autowired
	EventoReport eventoReport;

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		er.save(evento);
		attributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso!");
		return "redirect:/cadastrarEvento";
	}

	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long codigo) {
		Evento evento = er.findByCodigo(codigo);
		if(cr.findByEvento(evento) != null) {
			Iterable<Convidado> convidados = cr.findByEvento(evento);
			cr.deleteAll(convidados);
		}
		er.delete(evento);
		return "redirect:/eventos";
	}
	
	@RequestMapping(value = "/editarEvento", method = RequestMethod.GET)
	public ModelAndView editarEvento() {
		ModelAndView modelAndView = new ModelAndView("evento/editarEvento");
		Iterable<Evento> eventos = er.findAll();
		modelAndView.addObject("eventoobj", new Evento());
		modelAndView.addObject("eventos", eventos);
		return modelAndView;
	}
	
	@RequestMapping(value = "/editarEventoByCodigo/{codigo}", method = RequestMethod.GET)
	public ModelAndView editarEventoByCodigo(@PathVariable("codigo") long codigo) {
		
		ModelAndView modelAndView = new ModelAndView("evento/editarEvento");
		Evento evento = er.findByCodigo(codigo);
		Iterable<Evento> eventos = er.findAll();
		modelAndView.addObject("eventoobj", evento);
		modelAndView.addObject("eventos", eventos);
		return modelAndView;
	}
	
	@RequestMapping(value = "/editarEventoByCodigo/{codigo}", method = RequestMethod.POST)
	public String editarEventoTeste(Evento evento) {
		er.save(evento);
		return "redirect:/editarEvento";
	}
	
	// Arrea Detalhes do evento
	
	@RequestMapping("/imprimerelatorio")
	public void imprimePDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Iterable<Evento> iterable = er.findAll();
		List<Evento> listDados = (List<Evento>) iterable;
		
		byte[] pdf = eventoReport.gerarRelatorio(listDados, "evento", request.getServletContext());
		
		response.setContentLength(pdf.length);
		
		response.setContentType("application/octet-stream");
		
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		
		response.setHeader(headerKey, headerValue);
		
		response.getOutputStream().write(pdf);
	}
	
	
	
	@RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		Evento evento = er.findByCodigo(codigo);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		mv.addObject("evento", evento);

		Iterable<Convidado> convidados = cr.findByEvento(evento);
		mv.addObject("convidados", convidados);

		return mv;
	}

	@RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable("codigo") long codigo, @Valid Convidado convidado,
			BindingResult result, RedirectAttributes attributes) {
		Evento evento = er.findByCodigo(codigo);
		convidado.setEvento(evento);
		cr.save(convidado);
		return "redirect:/{codigo}";
	}
	
	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado = cr.findByRg(rg);
		cr.delete(convidado);
		Evento evento = convidado.getEvento();
		long codigoLong = evento.getCodigo();
		String codigo = ""+ codigoLong;
		return "redirect:/"+codigo;
	}
}
