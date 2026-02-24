package lv.venta.fidi.model;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
//import lv.venta.model.base.BaseAuditEntity;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "GenresTable")
@Entity
public class Genre  {

	// Fields | Dati
	// =================
	@Setter(value = AccessLevel.NONE)
	@Id
	@Column(name = "genre_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long genreId;

	@NotBlank
	@Column(name = "Name", nullable = false, unique = true)
	private String name;

	// Connections | Saites
	// =================
	@ManyToMany(mappedBy = "genres")
	private Collection<Movie> movies;

	// Constructors | Konstruktori
	// =================
	public Genre(String name) {
		setName(name);
	}
}