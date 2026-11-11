package ar.edu.pp1.pedidos.entidad;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Tabla usuario: empleados y la administradora. El rol es 'EMPLEADO' o 'ADMINISTRADOR'.
@Entity
@Table(name = "usuario")
public class Usuario {

    public static final String EMPLEADO = "EMPLEADO";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato valido")
    @Column(nullable = false, length = 120, unique = true)
    private String correo;

    // WRITE_ONLY: la contrasena se puede recibir en el JSON, pero nunca se devuelve
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String contrasena;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private Boolean activo;

    public Usuario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
