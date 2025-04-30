package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class OwnerServiceTest {
    @MockitoBean
    private OwnerRepository ownerRepository;

    @Autowired
    OwnerService ownerService;

    @Test
    public void testSaveOwner() {
        Owner owner = new Owner(
                "mockOwner",
                "mockDisplayName",
                "mockEmail",
                "mockPhone");

        when(ownerRepository.save(owner)).thenReturn(owner);
        ownerService.saveOwner(owner);

        verify(ownerRepository, times(1)).save(owner);
    }
}
