package br.com.ecommerce.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AbstractEmailService {

	private static final Logger LOG = LoggerFactory.getLogger(MockEmailService.class);

	@Override
	public void sendEmail(SimpleMailMessage simpleMailMessage) {
		LOG.info("Simulando envio de e-mail...");
		LOG.info(simpleMailMessage.toString());
		LOG.info("E-mail enviado!");
	}

}