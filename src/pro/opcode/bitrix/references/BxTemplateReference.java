package pro.opcode.bitrix.references;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.api.BxCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BxTemplateReference extends BxReference
{
	private enum bxTemplatePart {
		HEADER,
		FOOTER
	}

	public static HashMap<String, bxTemplatePart> bxTemplateReferenceTargets = new HashMap<String, bxTemplatePart>(); static {
		bxTemplateReferenceTargets.put("/bitrix/header.php", bxTemplatePart.HEADER);
		bxTemplateReferenceTargets.put("/bitrix/footer.php", bxTemplatePart.FOOTER);
	}

	public BxTemplateReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		StringLiteralExpression element = (StringLiteralExpression) getElement();
		VirtualFile[] siteTemplates = new BxCore(getElement().getProject()).getSiteTemplatesComponents(bxTemplateReferenceTargets.get(element.getContents()).name().toLowerCase() + ".php");
		if (siteTemplates.length > 0)
			return element.getManager().findFile(siteTemplates[0]);

		return null;
	}

	@NotNull
	public PsiReference[] getReference() {
		List<PsiReference> references = new ArrayList<PsiReference>();
		for (final VirtualFile template : new BxCore(getElement().getProject()).getSiteTemplatesComponents(bxTemplateReferenceTargets.get(((StringLiteralExpression) getElement()).getContents()).name().toLowerCase() + ".php")) {
			references.add(new BxReference(getElement()) {
				@Nullable @Override public PsiElement resolve() {
					return getElement().getManager().findFile(template);
				}
			});
		}

		return references.toArray(new PsiReference[references.size()]);
	}

	@Override
	public boolean isSoft() {
		return true;
	}
}
