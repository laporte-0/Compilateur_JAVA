// CHECKSTYLE:OFF
package phase.b_syntax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import compil.Compiler;

/**
 * Test de la phase syntaxique du compilateur sur chaîne de caractères en entrée
 * et sur fichiers du répertoire {@code src/test/resources/Jalons}.
 * 
 * @author Denis Conan
 */
final class SuccessfulMilestonesTest extends common.SuccessfulMilestonesTest {
	@BeforeEach
	void setUp() {
		// retirer des commentaires la ligne suivante pour moins d'affichages
		// Debug.noLogging();
		// s'arrêter après l'analyse syntaxique
		Compiler.stopAfterSyntax();
	}

	@Test
	@Override
	protected void jalonString1() {
		super.jalonString1();
	}

	@Test@Disabled
	@Override
	protected void jalonString2() {
		super.jalonString2();
	}

	@Test@Disabled
	@Override
	protected void jalonString3() {
		super.jalonString3();
	}

	@Test@Disabled
	@Override
	protected void jalonString4() {
		super.jalonString4();
	}

	@Test@Disabled
	@Override
	protected void jalonString5() {
		super.jalonString5();
	}

	@Test@Disabled
	@Override
	protected void jalonString6() {
		super.jalonString6();
	}

	@Test@Disabled
	@Override
	protected void jalonString7() {
		super.jalonString7();
	}

	// pas de jalon 8

	@Test@Disabled
	@Override
	protected void jalonString9() {
		super.jalonString9();
	}

	@Test
	@Override
	protected void jalonFile1() {
		super.jalonFile1();
	}

	@Test
	@Override
	protected void jalonFile2() {
		super.jalonFile2();
	}

	@Test@Disabled
	@Override
	protected void jalonFile3() {
		super.jalonFile3();
	}

	@Test@Disabled
	@Override
	protected void jalonFile4() {
		super.jalonFile4();
	}

	@Test
	@Override
	protected void jalonFile5() {
		super.jalonFile5();
	}

	@Test
	@Override
	protected void jalonFile6() {
		super.jalonFile6();
	}

	@Test
	@Override
	protected void jalonFile7() {
		super.jalonFile7();
	}

	// pas de jalon 8

	@Test
	@Override
	protected void jalonFile9() {
		super.jalonFile9();
	}
}
