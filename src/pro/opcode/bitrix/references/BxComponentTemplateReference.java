package pro.opcode.bitrix.references;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.api.BxCore;

public class BxComponentTemplateReference extends BxReference
{
	public BxComponentTemplateReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		VirtualFile componentTemplateFile = BxCore.getComponent(getElement()).getComponentTemplateFile("{template.php,template.twig,template.tpl}"); if (componentTemplateFile != null)
			return getElement().getManager().findFile(componentTemplateFile);
		return null;
	}
}
