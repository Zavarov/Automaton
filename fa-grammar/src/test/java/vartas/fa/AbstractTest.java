/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.fa;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import vartas.fa.finiteautomaton._ast.ASTFiniteAutomaton;
import vartas.fa.finiteautomaton._symboltable.FiniteAutomatonLanguage;
import vartas.fa.finiteautomaton._symboltable.FiniteAutomatonSymbol;
import vartas.fa.finiteautomaton._cocos.FiniteAutomatonCoCos;
import org.junit.BeforeClass;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;


public abstract class AbstractTest {

    protected static final String EXAMPLES_PATH = "src/test/resources/fa";

    @BeforeClass
    public static void init() {
        Log.init();
        Log.enableFailQuick(false);
    }

    protected static void checkForFindings(String... findings){
        List<Finding> expectedFindings = Arrays.stream(findings).map(Finding::error).collect(Collectors.toList());
        assertThat(Log.getFindings()).containsExactlyElementsOf(expectedFindings);
    }

    protected static void checkCoCos(ASTFiniteAutomaton ast){
        FiniteAutomatonCoCos.getCheckerForAllCoCos().checkAll(ast);

        assertTrue(Log.getFindings().isEmpty());
    }

    protected static ASTFiniteAutomaton parseValidModel(String modelFile){
        return parseModel(EXAMPLES_PATH +
                        FileSystems.getDefault().getSeparator() +
                        "valid",
                        modelFile);
    }

    protected static ASTFiniteAutomaton parseInvalidModel(String modelFile) {
        return parseModel(EXAMPLES_PATH +
                FileSystems.getDefault().getSeparator() +
                "invalid",
                modelFile);
    }

    protected static ASTFiniteAutomaton parseModel(String modelFilePath, String modelFile){
        GlobalScope scope = createGlobalScope(modelFilePath);
        Optional<FiniteAutomatonSymbol> symbol = scope.resolve(modelFile, FiniteAutomatonSymbol.KIND);

        assertThat(symbol).isPresent();
        assertThat(symbol.get().getFiniteAutomatonNode()).isPresent();

        return symbol.get().getFiniteAutomatonNode().get();
    }

    protected static GlobalScope createGlobalScope(String modelFilePath){
        FiniteAutomatonLanguage language = new FiniteAutomatonLanguage();

        ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
        resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

        ModelPath modelPath = new ModelPath(Paths.get(modelFilePath));

        return new GlobalScope(modelPath, language, resolvingConfiguration);
    }
}
