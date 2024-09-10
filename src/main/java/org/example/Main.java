package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example-unit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            // Iniciar la transacción
            entityManager.getTransaction().begin();

            // Crear categorías
            Categoria verduraleria = Categoria.builder()
                    .denominacion("Verduralería")
                    .build();

            Categoria carniceria = Categoria.builder()
                    .denominacion("Carnicería")
                    .build();

            Categoria conVencimiento = Categoria.builder()
                    .denominacion("Con vencimiento")
                    .build();

            // Crear artículos
            Articulo tomate = Articulo.builder()
                    .cantidad(10)
                    .denominacion("Tomate")
                    .precio(20)
                    .build();
            tomate.getCategorias().add(verduraleria);
            tomate.getCategorias().add(conVencimiento);

            Articulo lechuga = Articulo.builder()
                    .cantidad(20)
                    .denominacion("Lechuga")
                    .precio(10)
                    .build();
            lechuga.getCategorias().add(verduraleria);
            lechuga.getCategorias().add(conVencimiento);

            Articulo carneVacuna = Articulo.builder()
                    .cantidad(5)
                    .denominacion("Carne Vacuna")
                    .precio(100)
                    .build();
            carneVacuna.getCategorias().add(carniceria);
            carneVacuna.getCategorias().add(conVencimiento);

            Articulo pollo = Articulo.builder()
                    .cantidad(10)
                    .denominacion("Pollo")
                    .precio(50)
                    .build();
            pollo.getCategorias().add(carniceria);
            pollo.getCategorias().add(conVencimiento);

            // Crear cliente y domicilio
            Domicilio domicilio = Domicilio.builder()
                    .nombreCalle("Paso de los Andes")
                    .numero(245)
                    .build();

            Cliente cliente = Cliente.builder()
                    .nombre("Agustin")
                    .apellido("Leyes")
                    .dni(40123456)
                    .domicilio(domicilio)
                    .build();

            // Crear detalles de factura
            DetalleFactura detalleFactura1 = DetalleFactura.builder()
                    .cantidad(2)
                    .articulo(tomate)
                    .subtotal(40)
                    .build();

            DetalleFactura detalleFactura2 = DetalleFactura.builder()
                    .cantidad(1)
                    .articulo(carneVacuna)
                    .subtotal(100)
                    .build();

            Set<DetalleFactura> detalleFacturas = new HashSet<>();
            detalleFacturas.add(detalleFactura1);
            detalleFacturas.add(detalleFactura2);

            // Crear factura
            Factura factura = Factura.builder()
                    .numero(98765)
                    .fecha(Date.valueOf("2024-09-04"))
                    .cliente(cliente)
                    .detalleFacturas(detalleFacturas)
                    .build();

            // Persistir la factura
            entityManager.persist(factura);

            // Confirmar la transacción
            entityManager.getTransaction().commit();

            // Factura persistida, buscarla desde la base de datos
            Factura facturaDB = entityManager.find(Factura.class, factura.getId());
            System.out.println("Factura persistida: " + facturaDB.getNumero());

            // Iniciar otra transacción para actualizar la factura
            entityManager.getTransaction().begin();

            // Actualizar el número de la factura
            facturaDB.setNumero(4321);
            entityManager.merge(facturaDB);

            // Confirmar la actualización
            entityManager.getTransaction().commit();

            // Verificar la actualización
            Factura facturaDB2 = entityManager.find(Factura.class, factura.getId());
            System.out.println("Factura actualizada: " + facturaDB2.getNumero());

            // Iniciar otra transacción para eliminar la factura
            entityManager.getTransaction().begin();

            // Eliminar la factura
            entityManager.remove(facturaDB2);

            // Confirmar la eliminación
            entityManager.getTransaction().commit();

            // Verificar si la factura fue eliminada
            Factura facturaDB3 = entityManager.find(Factura.class, factura.getId());
            if (facturaDB3 == null) {
                System.out.println("Factura eliminada: " + facturaDB2.getNumero());
            }

        } catch (Exception e) {
            // Si hay algún error, hacer rollback de la transacción
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.out.println("Error: " + e.getMessage());
            System.out.println("No se pudo grabar la clase Factura");
        } finally {
            // Cerrar el EntityManager y el EntityManagerFactory
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}
