package pro.opcode.bitrix.types;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class BxSuperglobalsProvider extends CompletionContributor implements PhpTypeProvider2
{
	private static final HashMap<String, String> bxSuperglobals = new HashMap<String, String>(); static {
		bxSuperglobals.put("APPLICATION", "CMain");
		bxSuperglobals.put("USER", "CUser");
		bxSuperglobals.put("DB", "CDatabase");
	}

	public BxSuperglobalsProvider() {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(PhpTokenTypes.VARIABLE).withLanguage(PhpLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
				for (String variable : bxSuperglobals.keySet())
					resultSet.addElement(LookupElementBuilder.create("$" + variable));
			}
		});
	}

	@Override
	public char getKey() {
		return 'Я';
	}

	@Nullable
	@Override
	public String getType(PsiElement e) {
		if (e instanceof Variable)
			return bxSuperglobals.get(((Variable) e).getName());

		return null;
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {
		if (bxSuperglobals.containsValue(expression))
			return PhpIndex.getInstance(project).getClassesByFQN(expression);

		return Collections.emptySet();
	}
}