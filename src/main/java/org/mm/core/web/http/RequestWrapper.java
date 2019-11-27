package org.mm.core.web.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

	private final String body;

	public RequestWrapper(HttpServletRequest request) {
		super(request);
		
		StringBuilder stringBuilder = new StringBuilder();
		String inputLine;
		try {
			BufferedReader br = request.getReader();
			while ((inputLine = br.readLine()) != null) {
				stringBuilder.append(inputLine);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
		
		body = stringBuilder.toString();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {
			@Override
			public boolean isFinished() {
				return false;
			}
			@Override
			public boolean isReady() {
				return false;
			}
			@Override
			public void setReadListener(ReadListener readListener) {
			}
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		
		return servletInputStream;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}

	public String getBody() {
		return this.body;
	}

}
