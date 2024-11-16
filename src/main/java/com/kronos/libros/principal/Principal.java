package com.kronos.libros.principal;

import com.kronos.libros.model.Autor;
import com.kronos.libros.model.Datos;
import com.kronos.libros.model.DatosAutor;
import com.kronos.libros.model.Libro;
import com.kronos.libros.repository.AutorRepository;
import com.kronos.libros.repository.LibroRepository;
import com.kronos.libros.service.ConsumoAPI;
import com.kronos.libros.service.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;

        while (opcion != 0){
            var menu = """
                ***** MENÚ *****
                1- Buscar libro por título
                2- Listar libros registrados
                3- Listar autores registrados
                4- listar autores vivos en un determinado año
                5- Listar libros por idioma
                0- Salir
                """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnUnAnio();
                    break;
                case 5:
                    listarLibrosPorIdiomas();
                    break;
                case 0:
                    System.out.println("Programa terminado");
                    break;
                default:
                    System.out.println("Opción incorrecta");
            }
        }
    }

    private void listarLibrosPorIdiomas() {
        mostrarIdiomas();
        var idioma = teclado.nextLine();
        List<Libro> libros = libroRepository.buscarLibroPorIdioma(idioma);
        libros.forEach(l -> l.mostrarLibro());
    }

    public void mostrarIdiomas(){
        var idiomas = """
                Ingrese el idioma para ver los libros:
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués
                """;
        System.out.println(idiomas);
    }

    private void listarAutoresVivosEnUnAnio() {
        System.out.println("Ingresa el año para ver los autores vivos: ");
        var anio = teclado.nextInt();
        List<Autor> autores = autorRepository.autoresVivosEnUnAnio(anio);
        autores.forEach(a -> a.mostrarDatos());
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(a -> a.mostrarDatos());
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(l -> l.mostrarLibro());
    }

    public Datos getDatosLibro(){
        System.out.println("Ingrese el nombre del libro: ");
        var nombreLibro = teclado.nextLine();
        var libroBuscado = verificarExisteciaDelLibro(nombreLibro);
        if(libroBuscado.isPresent()){
            return null;
        }

        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));

        // datos: lista de libros que coinciden con el nombre ingresado
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private Optional<Libro> verificarExisteciaDelLibro(String nombreLibro) {
        return libroRepository.buscarLibroPorNombre(nombreLibro);
    }

    public void buscarLibro(){
        Datos datos = getDatosLibro();

        if(datos != null && datos.resultados().isEmpty()){
            System.out.println("Libro no encontrado");
            return;
        }

        if(datos == null){
            System.out.println("El libro ya existe en la BD");
            return;
        }

        DatosAutor datosAutor = datos.resultados().get(0).autor().get(0);
        Autor autor = new Autor(datosAutor);

        Autor autorBuscado = buscarAutorPorNombre(autor.getNombre());
        if(autorBuscado == null){
            autorRepository.save(autor);
        }

        Long idAutor = autorRepository.buscarPorNombre(autor.getNombre()).getId();

        Libro libro = new Libro(idAutor, datos.resultados().get(0));
        libroRepository.save(libro);
        System.out.println("Libro guardado con éxito!");
        libro.mostrarLibro();
    }

    private Autor buscarAutorPorNombre(String nombre) {
        return autorRepository.buscarPorNombre(nombre);
    }
}
