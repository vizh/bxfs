package pro.opcode.bitrix.references;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.api.BxCore;

public class BxComponentIncludeReference extends BxReference
{
	public BxComponentIncludeReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		BxCore bitrix = new BxCore(getElement().getProject());
		String inclusionType = null;

		for (ArrayHashElement arElement : ((ArrayCreationExpression) getElement().getParent().getParent().getParent()).getHashElements()) {
			if (arElement.getKey() instanceof StringLiteralExpression && ((StringLiteralExpression) arElement.getKey()).getContents().equals("AREA_FILE_SHOW") && arElement.getValue() instanceof StringLiteralExpression)
				inclusionType = ((StringLiteralExpression) arElement.getValue()).getContents();
		}

		if (inclusionType != null) {
			VirtualFile componentTemplateFile = bitrix.getPageIncludeFile(
				getElement(),
				inclusionType.equals("page") ? "index" : "sect"
			);

			if (componentTemplateFile != null)
				return getElement().getManager().findFile(componentTemplateFile);
		}

		return null;
	}
}
