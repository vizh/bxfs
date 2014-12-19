package pro.opcode.bitrix;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BxReference implements PsiReference
{
	private PsiElement element;

	public BxReference(PsiElement element) {
		this.element = element;
	}

	@Override
	public PsiElement getElement() {
		return element;
	}

	@Override
	public TextRange getRangeInElement() {
		return new TextRange(1, getCanonicalText().length() - 1);
	}

	@Nullable
	@Override
	public abstract PsiElement resolve();

	@NotNull
	@Override
	public String getCanonicalText() {
		return element.getText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return resolve();
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		PsiElement resolvedElement = resolve();
		return resolvedElement != null
			&& resolvedElement.equals(element);
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return EMPTY_ARRAY;
	}

	@Override
	public boolean isSoft() {
		return false;
	}

	public PsiReference[] getReference() {
		return resolve() == null ? EMPTY_ARRAY : new PsiReference[]{this};
	}
}
