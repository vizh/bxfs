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
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BxSuperglobalsProvider extends CompletionContributor implements PhpTypeProvider2
{
	private static class BxSuperglobal {
		public String className;
		public List<String> scopeFileNames;

		public BxSuperglobal(String className, List<String> scopeFileNames) {
			this.className = className;
			this.scopeFileNames = scopeFileNames;
		}
	}

	private static final HashMap<String, BxSuperglobal> bxSuperglobals = new HashMap<String, BxSuperglobal>(); static {
		bxSuperglobals.put("APPLICATION", 				new BxSuperglobal("CMain",						null));
		bxSuperglobals.put("USER",						new BxSuperglobal("CUser",						null));
		bxSuperglobals.put("DB",						new BxSuperglobal("CDatabase",					null));
		bxSuperglobals.put("this",						new BxSuperglobal("CBitrixComponentTemplate",	Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("component",					new BxSuperglobal("CBitrixComponent",			Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("arResult",					new BxSuperglobal("GLOBALS",					Arrays.asList("template.php", "result_modifier.php", "component_epilog.php", "component.php"					)));
		bxSuperglobals.put("arParams",					new BxSuperglobal("GLOBALS",					Arrays.asList("template.php", "result_modifier.php", "component_epilog.php", "component.php"					)));
		bxSuperglobals.put("arLangMessages",			new BxSuperglobal("GLOBALS",					Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("templateName",				new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("templateData",				new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("templateFile",				new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("templateFolder",			new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("parentTemplateFolder",		new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php"										)));
		bxSuperglobals.put("parentComponentName",		new BxSuperglobal("php_errormsg",				Arrays.asList(																 "component.php"					)));
		bxSuperglobals.put("parentComponentPath",		new BxSuperglobal("php_errormsg",				Arrays.asList(																 "component.php"					)));
		bxSuperglobals.put("parentComponentTemplate",	new BxSuperglobal("php_errormsg",				Arrays.asList(																 "component.php"					)));
		bxSuperglobals.put("componentPath",				new BxSuperglobal("php_errormsg",				Arrays.asList("template.php", "result_modifier.php", "component_epilog.php", "component.php", ".parameters.php"	)));
		bxSuperglobals.put("componentName",				new BxSuperglobal("php_errormsg",				Arrays.asList(																 "component.php", ".parameters.php"	)));
		bxSuperglobals.put("componentTemplate",			new BxSuperglobal("php_errormsg",				Arrays.asList(																 "component.php"					)));
		bxSuperglobals.put("templateProperties",		new BxSuperglobal("php_errormsg",				Arrays.asList(																				  ".parameters.php"	)));
		bxSuperglobals.put("arCurrentValues",			new BxSuperglobal("php_errormsg",				Arrays.asList(																				  ".parameters.php"	)));
		bxSuperglobals.put("arComponentParameters",		new BxSuperglobal("php_errormsg",				Arrays.asList(																				  ".parameters.php"	)));
	}

	public BxSuperglobalsProvider() {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(PhpTokenTypes.VARIABLE).withLanguage(PhpLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
				String currentFileName = parameters.getOriginalFile().getName();
				for (String variable : bxSuperglobals.keySet()) {
					BxSuperglobal superglobal = bxSuperglobals.get(variable);
					if (superglobal.scopeFileNames == null || superglobal.scopeFileNames.contains(currentFileName))
						resultSet.addElement(LookupElementBuilder.create("$" + variable));
				}
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
		if (e instanceof Variable) {
			BxSuperglobal superglobal = bxSuperglobals.get(((Variable) e).getName()); if (superglobal != null) {
				if (superglobal.scopeFileNames == null || superglobal.scopeFileNames.contains(e.getContainingFile().getName()))
					return superglobal.className;
			}
		}

		return null;
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {
		for (BxSuperglobal superglobal : bxSuperglobals.values())
			if (superglobal.className.equals(expression)) {
				if (superglobal.className.equals("GLOBALS") || superglobal.className.equals("php_errormsg")) {
					return Arrays.asList(PhpPsiElementFactory.createVariable(project, superglobal.className, true));
				} else {
					return PhpIndex.getInstance(project).getClassesByFQN(expression);
				}
			}

		return Collections.emptySet();
	}
}