package com.interviewai.admin;

import com.interviewai.support.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIT {

    @Autowired private MockMvc mockMvc;

    @Test
    void adminEndpointsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser(id = "u-1", roles = {"USER"})
    void nonAdminForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(id = "u-1", roles = {"USER", "ADMIN"})
    void adminCanFetchStatistics() throws Exception {
        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
