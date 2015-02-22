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
import java.util.List;

public class BxApplicationIncludeFileReference extends BxReference
{
	public BxApplicationIncludeFileReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		String path = ((StringLiteralExpression) getElement()).getContents();
		/* Если путь к файлу включаемой области является абсолютным, то делегируем работу BxPathReference */
		if (path.startsWith("/"))
			return new BxPathReference(getElement()).resolve();
		/* Работаем по алгоритму, описанному тут: http://dev.1c-bitrix.ru/api_help/main/reference/cmain/includefile.php */
		VirtualFile file = BxCore.findFile(getElement().getProject().getBaseDir(), "{local,bitrix}/templates/*/" + path); if (file == null)
			return null;

		return file.isDirectory() ? getElement().getManager().findDirectory(file) : getElement().getManager().findFile(file);
	}

	@NotNull
	public PsiReference[] getReference() {
		String path = ((StringLiteralExpression) getElement()).getContents();
		/* Если путь к файлу включаемой области является абсолютным, то делегируем работу BxPathReference */
		if (path.startsWith("/"))
			return new BxPathReference(getElement()).getReference();
		/* Собираем все возможные варианты ссылки на включаемый файл, которые только сможем найти */
		VirtualFile[] files = BxCore.findFiles(getElement().getProject().getBaseDir(), "{local,bitrix}/templates/*/" + path); if (files == null)
			return EMPTY_ARRAY;
		/* Строим список вариантов переходов на найденные файлы */
		List<PsiReference> references = new ArrayList<PsiReference>();
		for (final VirtualFile file : files) {
			references.add(new BxReference(getElement()) {
				@Nullable @Override public PsiElement resolve() {
					return getElement().getManager().findFile(file);
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
