package request.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.request.RequestClient;
import ru.practicum.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RequestClientUnitTest {

    private RequestClient requestClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        requestClient = new RequestClient(restTemplate); // простой конструктор
    }

    @Test
    void createRequest_shouldReturnOk() {
        ItemRequestDto dto = new ItemRequestDto("Нужна дрель");
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestClient.createRequest(userId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getUserRequests_shouldReturnOk() {
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestClient.getUserRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getAllRequests_shouldReturnOk() {
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestClient.getAllRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getRequestById_shouldReturnOk() {
        Long userId = 1L;
        Long requestId = 5L;

        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("ok", HttpStatus.OK);
        when(restTemplate.exchange(contains("/" + requestId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestClient.getRequestById(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restTemplate, times(1)).exchange(contains("/" + requestId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }
}

