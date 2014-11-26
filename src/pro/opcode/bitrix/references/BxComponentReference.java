package pro.opcode.bitrix.references;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.api.BxCore;

public class BxComponentReference extends BxReference
{
	public BxComponentReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		BxCore bitrix = new BxCore(getElement().getProject());
		VirtualFile componentFile = bitrix.getComponentSourceFile(getElement()); if (componentFile != null)
			return getElement().getManager().findFile(componentFile);
		return null;
	}
}
