package com.eventosapp.report;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class EventoReport implements Serializable{
	
	/*
	 * Retorna nosso PDF em byte para download no navegador
	 * */
	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext) throws Exception {

		/*
		 * Cria a lista de dados para o relatorio com nossa lista de Objetos para imprimir
		 * */
		JRBeanCollectionDataSource jrbcDataSource = new JRBeanCollectionDataSource(listDados);
		
		/*
		 * Carregar o caminho do arquivo jasper compilado
		 * */
		String pathJasper = servletContext.getRealPath("relatorios") 
				+ File.separator + relatorio + ".jasper";
		
		/*
		 * Carregar o arquivo jasper passando os dados
		 * */
		JasperPrint impressoraJasper = JasperFillManager
				.fillReport(pathJasper, new HashMap(), jrbcDataSource);
		
		/*
		 * Exposta para byte para download do pdf
		 * */
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}
