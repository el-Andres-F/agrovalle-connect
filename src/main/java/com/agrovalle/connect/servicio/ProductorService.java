package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.RegistroResponse;
import com.agrovalle.connect.dto.TipoUsuario;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.ProductorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business boundary for producer persistence and listing. */
@Service
public class ProductorService {

    private final ProductorRepository productorRepository;
    private final RegistroService registroService;

    public ProductorService(ProductorRepository productorRepository, RegistroService registroService) {
        this.productorRepository = productorRepository;
        this.registroService = registroService;
    }

    @Transactional
    public RegistroResponse crear(Productor productor) {
        RegistroRequest request = new RegistroRequest(TipoUsuario.PRODUCTOR, productor.getNombre(),
                productor.getUbicacion(), productor.getCedula(), null, null, null, null, productor.getContacto());
        return registroService.registrar(request);
    }

    @Transactional(readOnly = true)
    public List<Productor> listar() {
        return productorRepository.findAll();
    }
}
