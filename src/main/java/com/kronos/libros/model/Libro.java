package com.kronos.libros.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String idioma;
    private Integer descargas;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Autor autor;

    public Libro() {
    }

    public Libro(Long idAutor, DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.autor = new Autor(idAutor, datosLibro.autor().get(0));
        this.idioma = datosLibro.idiomas().get(0);
        this.descargas = datosLibro.numeroDeDescargas();
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }

    public void mostrarLibro(){
        System.out.println("----------- LIBRO -----------");
        System.out.println("Título: " + getTitulo());
        System.out.println("Autor: " + getAutor().getNombre());
        System.out.println("Idioma: " + getIdioma());
        System.out.println("Número de descargas: " + getDescargas());
        System.out.println("-----------------------------");
        System.out.println();
    }

    @Override
    public String toString() {
        return titulo;
    }
}
