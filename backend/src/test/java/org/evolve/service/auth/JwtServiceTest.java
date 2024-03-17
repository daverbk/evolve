package org.evolve.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.evolve.config.EvolveTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EvolveTestConfiguration.class)
public class JwtServiceTest {


}
