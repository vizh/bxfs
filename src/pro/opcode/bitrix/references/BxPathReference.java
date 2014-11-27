package pro.opcode.bitrix.references;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.api.BxCore;

public class BxPathReference extends BxReference
{
	public BxPathReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		StringLiteralExpression path = (StringLiteralExpression) getElement();
		VirtualFile baseDir = path.getContents().startsWith("/") ? path.getProject().getBaseDir() : path.getContainingFile().getVirtualFile().getParent();
		VirtualFile file = baseDir.findFileByRelativePath(path.getContents()); if (file == null)
			return null;
		return file.isDirectory() ? path.getManager().findDirectory(file) : path.getManager().findFile(file);
	}
}
