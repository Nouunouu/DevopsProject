
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import tn.esprit.spring.kaddem.services.UniversiteServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UniversiteServiceImplTest {
    @Mock
    private UniversiteRepository universiteRepository;
    @Mock
    private DepartementRepository departementRepository;
    @InjectMocks
    private UniversiteServiceImpl universiteService;
    private Universite universite;
    private Departement departement;
    @BeforeEach
    void setUp() {
        universite = new Universite();
        universite.setIdUniv(1);
        universite.setNomUniv("Test University");
        universite.setDepartements(new HashSet<>());
        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Test Department");
    }
    @Test
    void retrieveAllUniversites() {
        // Given
        Universite universite2 = new Universite();
        universite2.setIdUniv(2);
        universite2.setNomUniv("Second University");
        List<Universite> universities = Arrays.asList(universite, universite2);
        when(universiteRepository.findAll()).thenReturn(universities);
        // When
        List<Universite> result = universiteService.retrieveAllUniversites();
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(universite));
        assertTrue(result.contains(universite2));
        assertEquals("Test University", result.get(0).getNomUniv());
        assertEquals("Second University", result.get(1).getNomUniv());
        verify(universiteRepository, times(1)).findAll();
    }
    @Test
    void addUniversite() {
        // Given
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);
        // When
        Universite result = universiteService.addUniversite(universite);
        // Then
        assertNotNull(result);
        assertEquals(universite.getNomUniv(), result.getNomUniv());
        assertEquals(universite.getIdUniv(), result.getIdUniv());
        assertEquals(universite.getDepartements(), result.getDepartements());
        verify(universiteRepository, times(1)).save(universite);
    }
    @Test
    void updateUniversite() {
        // Given
        universite.setNomUniv("Updated University");
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);
        // When
        Universite result = universiteService.updateUniversite(universite);
        // Then
        assertNotNull(result);
        assertEquals("Updated University", result.getNomUniv());
        assertEquals(universite.getIdUniv(), result.getIdUniv());
        verify(universiteRepository, times(1)).save(universite);
    }
    @Test
    void retrieveUniversite() {
        // Given
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        // When
        Universite result = universiteService.retrieveUniversite(1);
        // Then
        assertNotNull(result);
        assertEquals(universite.getNomUniv(), result.getNomUniv());
        assertEquals(universite.getIdUniv(), result.getIdUniv());
        assertEquals(universite.getDepartements(), result.getDepartements());
        verify(universiteRepository, times(1)).findById(1);
    }
    @Test
    void retrieveUniversite_NotFound() {
        // Given
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> {
            universiteService.retrieveUniversite(999);
        });
        verify(universiteRepository, times(1)).findById(999);
    }
    @Test
    void deleteUniversite() {
        // Given
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        // When
        universiteService.deleteUniversite(1);
        // Then
        verify(universiteRepository, times(1)).findById(1);
        verify(universiteRepository, times(1)).delete(universite);
    }
    @Test
    void assignUniversiteToDepartement() {
        // Given
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(universiteRepository.save(any(Universite.class))).thenReturn(universite);
        // When
        universiteService.assignUniversiteToDepartement(1, 1);
        // Then
        verify(universiteRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(universiteRepository, times(1)).save(universite);
        assertTrue(universite.getDepartements().contains(departement));
    }
    @Test
    void assignUniversiteToDepartement_UniversiteNotFound() {
        // Given
        when(universiteRepository.findById(999)).thenReturn(Optional.empty());
        // Then
        assertThrows(NullPointerException.class, () -> {
            universiteService.assignUniversiteToDepartement(999, 1);
        });
        verify(universiteRepository, times(1)).findById(999);
        verify(departementRepository, never()).findById(any());
    }
    @Test
    void retrieveDepartementsByUniversite() {
        // Given
        Set<Departement> departments = new HashSet<>(Arrays.asList(departement));
        universite.setDepartements(departments);
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        // When
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(departement));
        assertEquals("Test Department", result.iterator().next().getNomDepart());
        verify(universiteRepository, times(1)).findById(1);
    }
    @Test
    void retrieveDepartementsByUniversite_EmptyDepartements() {
        // Given
        universite.setDepartements(new HashSet<>());
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        // When
        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(universiteRepository, times(1)).findById(1);
    }
}