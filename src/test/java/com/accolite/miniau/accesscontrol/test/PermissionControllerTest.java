package com.accolite.miniau.accesscontrol.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/springDispatcherServlet-servlet.xml")
public class PermissionControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		// verifyRootWacSupport();
	}

	@Test
	public void testGetAllPermissions() throws Exception {
		this.mockMvc.perform(get("/api/permissions").accept(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk());
	}
}