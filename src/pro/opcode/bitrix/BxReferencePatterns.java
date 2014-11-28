package pro.opcode.bitrix;

import com.intellij.patterns.InitialPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.injection.PhpElementPattern;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.api.BxCore;

public class BxReferencePatterns
{
	/**
	 * Capturing first parameter of $APPLICATION->IncludeComponent() call
	 */
	public static PhpElementPattern.Capture<StringLiteralExpression> bxComponentReference() {
		return new PhpElementPattern.Capture<StringLiteralExpression>(new InitialPatternCondition<StringLiteralExpression>(StringLiteralExpression.class) {
			@Override
			public boolean accepts(@Nullable Object o, ProcessingContext context) {
				assert o != null && (o instanceof StringLiteralExpression || o instanceof LeafPsiElement);
				/* LeafPsiElement - это недописанный элемент, которому необходим автокомплит. Сам элемент - его предок. */
				if (o instanceof LeafPsiElement)
					o = ((LeafPsiElement) o).getParent();

				return isValidComponentCall(o)
					&& isParameterDepth(o, 1);
			}
		});
	}

	/**
	 * Capturing second parameter of $APPLICATION->IncludeComponent() call
	 */
	public static PhpElementPattern.Capture<StringLiteralExpression> bxComponentTemplateReference() {
		return new PhpElementPattern.Capture<StringLiteralExpression>(new InitialPatternCondition<StringLiteralExpression>(StringLiteralExpression.class) {
			@Override
			public boolean accepts(@Nullable Object o, ProcessingContext context) {
				assert o != null && o instanceof StringLiteralExpression;
				return isValidComponentCall(o)
					&& isParameterDepth(o, 2);
			}
		});
	}

	/**
	 * Capture of usage some includes
	 */
	public static PhpElementPattern.Capture<StringLiteralExpression> bxComponentMainIncludeReference() {
		return new PhpElementPattern.Capture<StringLiteralExpression>(new InitialPatternCondition<StringLiteralExpression>(StringLiteralExpression.class) {
			@Override
			public boolean accepts(@Nullable Object o, ProcessingContext context) {
				assert o != null && o instanceof StringLiteralExpression;
				/* Filter for non array value elements */
				PhpPsiElement arValue = (PhpPsiElement) ((PhpPsiElement) o).getParent(); if (arValue.getNode().getElementType() != PhpElementTypes.ARRAY_VALUE) {
//					System.out.println("Текущий элемент не является значением массива");
					return false;
				}

				/* Элемент должен быть значением ассоциативного массива */
				PhpPsiElement arElement = (PhpPsiElement) arValue.getParent(); if (arElement.getNode().getElementType() != PhpElementTypes.HASH_ARRAY_ELEMENT) {
//					System.out.println("Текущий элемент не является значением ассоциативного массива");
					return false;
				}

				/* Элемент должен быть значением строкового ключа AREA_FILE_SUFFIX ассоциативного массива */
				PhpPsiElement arKey = ((ArrayHashElement)arElement).getKey(); if (arKey == null || arKey.getNode().getElementType() != PhpElementTypes.STRING || !((StringLiteralExpression) arKey).getContents().equals("AREA_FILE_SUFFIX")) {
//					System.out.println("Текущий элемент не является значением строкового ключа AREA_FILE_SUFFIX ассоциативного массива");
					return false;
				}

				/* Элемент должен быть значением ключа массива, который является параметров вызова $APPLICATION->IncludeComponent('bitrix:main.include') */
				if (!isValidComponentCall(arKey.getParent().getParent().getParent(), "bitrix:main.include")) {
//					System.out.println("Элемент должен быть значением ключа массива, который является параметров вызова $APPLICATION->IncludeComponent('bitrix:main.include')");
					return false;
				}

				return true;
			}
		});
	}

	/**
	 * Захват строк, содержащих пути к файлам / папкам
	 */
	public static PhpElementPattern.Capture<StringLiteralExpression> bxPathReference() {
		return new PhpElementPattern.Capture<StringLiteralExpression>(new InitialPatternCondition<StringLiteralExpression>(StringLiteralExpression.class) {
			@Override
			public boolean accepts(@Nullable Object o, ProcessingContext context) {
				assert o != null && o instanceof StringLiteralExpression;
				return (BxCore.isFilenameValid(((StringLiteralExpression) o).getContents()));
			}
		});
	}

	/**
	 * Is the element is a parameter of $APPLICATION->IncludeComponent() call
	 */
	private static boolean isValidComponentCall(Object o, String component) {
		PsiElement parameters = ((PsiElement) o).getParent(); if (parameters instanceof ParameterList) {
			if (component != null) {
				PsiElement[] params = ((ParameterList) parameters).getParameters(); if (params.length == 0 || params[0].getNode().getElementType() != PhpElementTypes.STRING || !((StringLiteralExpression) params[0]).getContents().equals(component))
					return false;
			}
			PsiElement method = parameters.getParent(); if (method instanceof MethodReference) {
				PsiElement clazz = ((MethodReference) method).getClassReference(); if (clazz instanceof Variable) {
					return !((MethodReference) method).isStatic()
						&& "APPLICATION".equals(((Variable) clazz).getName())
						&& "IncludeComponent".equals(((MethodReference) method).getName());
				}
			}
		}
		return false;
	}

	private static boolean isValidComponentCall(Object o) {
		return isValidComponentCall(o, null);
	}

	/**
	 * Is the element have expected position in method parameters list
	 */
	private static boolean isParameterDepth(Object o, int depth) {
		PsiElement[] parameters = ((ParameterList) ((PsiElement) o).getParent()).getParameters();
		return parameters.length >= depth && parameters[depth - 1].equals(o);
	}
}
