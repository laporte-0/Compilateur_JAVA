package phase.b_syntax.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import compil.util.AstLocations;

/**
 * Cette classe ne sert pas dans le code, mais est ajouté pour montrer la
 * différence avec le <i>record</i> {@link Axiom}.
 * 
 * @author Denis Conan
 */
public class AxiomClasse implements AstNode {
	/**
	 * le label.
	 */
	private final String label;
	/**
	 * les nœuds enfants.
	 */
	private final List<AstNode> enfants;
	/**
	 * les positions de début et de fin du nœud dans le fichier source.
	 */
	private final AstLocations locations;
	/**
	 * la classe conventionnelle {@code main}.
	 */
	private final KlassMain klassMain;
	/**
	 * les autres classes.
	 */
	private final AstList<Klass> klassList;

	/**
	 * construit un enregistrement avec des valeurs par défaut pour les premiers
	 * attributs (label, etc.).
	 * 
	 * @param label     le label.
	 * @param enfants   les nœuds enfants.
	 * @param locations les positions de début et de fin du nœud dans le fichier
	 *                  source.
	 * @param klassMain la classe conventionnelle {@code main}.
	 * @param klassList les autres classes.
	 */
	public AxiomClasse(final String label, final List<AstNode> enfants, final AstLocations locations,
			final KlassMain klassMain, final AstList<Klass> klassList) {
		this.label = label;
		this.enfants = enfants;
		this.locations = locations;
		this.klassMain = klassMain;
		this.klassList = klassList;
	}

	/**
	 * obtient le label.
	 * 
	 * @return le label.
	 */
	public String label() {
		return label;
	}

	/**
	 * obtient les enfants du nœud.
	 * 
	 * @return les enfants.
	 */
	public List<AstNode> enfants() {
		return enfants;
	}

	/**
	 * obtient les positions du nœud.
	 * 
	 * @return les positions.
	 */
	public AstLocations locations() {
		return locations;
	}

	/**
	 * obtient la classe avec la méthode {@code main}.
	 * 
	 * @return la classe avec la méthode {@code main}.
	 */
	public KlassMain klassMain() {
		return klassMain;
	}

	/**
	 * obtient la liste des autres classes.
	 * 
	 * @return la liste des autres classes.
	 */
	public AstList<Klass> klassList() {
		return klassList;
	}

	@Override
	public void accept(final AstVisitor v) {
		// ...
	}

	@Override
	public int hashCode() {
		return Objects.hash(enfants, klassList, klassMain, label, locations);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AxiomClasse other)) {
			return false;
		}
		return Objects.equals(enfants, other.enfants) && Objects.equals(klassList, other.klassList)
				&& Objects.equals(klassMain, other.klassMain) && Objects.equals(label, other.label)
				&& Objects.equals(locations, other.locations);
	}

	@Override
	public String toString() {
		return print();
	}

	/**
	 * construit un enregistrement avec des valeurs par défaut pour les premiers
	 * attributs (label, etc.).
	 * 
	 * @param klassMain la classe avec la méthode {@code main}.
	 * @param klassList la liste des autres classes.
	 * @return l'objet construit.
	 */
	public static AxiomClasse build(final KlassMain klassMain, final AstList<Klass> klassList) {
		return new AxiomClasse(AxiomClasse.class.getSimpleName(), Arrays.asList(klassMain, klassList),
				new AstLocations(), klassMain, klassList);
	}
}
