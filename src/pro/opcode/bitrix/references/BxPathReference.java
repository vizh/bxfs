package pro.opcode.bitrix.references;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;

/* toDo: В процессе переименования файлов с такими ссылками как 'ind'.'ex.c'.'ss' теряется их связь с файлом */
/* toDo: Найти способ определения того, что метод checkComplicatedElement вообще необходим. Есть подозрение, что от него нет проку. */
public class BxPathReference extends BxReference
{
	public BxPathReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return resolve((StringLiteralExpression) getElement());
	}

	@Nullable
	public PsiElement resolve(StringLiteralExpression element) {
		String path = element.getContents();
		/* Если строка является частью объединения строк, то соберём кусочки паззла */
		if (element.getParent() instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) element.getParent();
			if (binaryExpression.getOperationType() != null && binaryExpression.getOperationType().toString().equals("dot")) {
				String pathResolved = resolveConcatenation(binaryExpression); if (pathResolved != null)
					path = pathResolved.replace("//", "/"); /* DOCUMENT_URL может оканчиваться, а может и не оканчиваться на /, потому есть свобода сделать это */
			}
		}

		VirtualFile baseDir = path.startsWith("/")
			? element.getProject().getBaseDir()
			: element.getContainingFile().getVirtualFile().getParent();

		if (baseDir == null)
			return null;

		VirtualFile file
			= baseDir.findFileByRelativePath(path);

		if (file == null)
			return null;

		return file.isDirectory()
			? element.getManager().findDirectory(file)
			: element.getManager().findFile(file);
	}

	public String resolveConcatenation(BinaryExpression binaryExpression) {
		String result = "";
		for (PsiElement operand : binaryExpression.getChildren()) {
			/* Вложенные конкатенации */
			if (operand instanceof BinaryExpression) {
				String recursionResult = resolveConcatenation((BinaryExpression) operand); if (recursionResult == null)
					return null;
				result += recursionResult;
				continue;
			}
			/* Простые строки */
			if (operand instanceof StringLiteralExpression) {
				result += ((StringLiteralExpression) operand).getContents();
				continue;
			}
			/* Переменные $_SERVER['DOCUMENT_ROOT'] */
			if (operand instanceof ArrayAccessExpression && operand.getText().replace('"', '\'').equals("$_SERVER['DOCUMENT_ROOT']")) {
				result += "/";
				continue;
				/* Рабочий, но уж очень громоздкий способ... Простое сравнение строк менее ресурсоёмко.
				PsiElement value = ((ArrayAccessExpression) operand).getValue(); if (value != null && value instanceof Variable) {
					String valueName = ((Variable) value).getName(); if (valueName != null && valueName.equals("_SERVER")) {
						PsiElement index = ((ArrayAccessExpression) operand).getIndex(); if (index != null) {
							PsiElement indexValue = ((ArrayIndex) index).getValue(); if (indexValue != null && indexValue instanceof StringLiteralExpression) {
								String indexName = ((StringLiteralExpression) indexValue).getContents(); if (indexName.equals("DOCUMENT_ROOT")) {
									result += "/";
									continue;
								}
							}
						}

					}
				}
				*/
			}
			/* Вызов $APPLICATION->sDirPath */
			if (operand instanceof FieldReference && operand.getText().toLowerCase().equals("$application->sdirpath")) {
				String resolvedPath = operand.getContainingFile().getContainingDirectory().getVirtualFile().getCanonicalPath(); if (resolvedPath == null)
					return null;
				result += resolvedPath.replace(operand.getProject().getBasePath(), "") + "/";
				continue;
			}
			/* Вызов $APPLICATION->GetCurDir() */
			if (operand instanceof MethodReference && operand.getText().toLowerCase().equals("$application->getcurdir()")) {
				String resolvedPath = operand.getContainingFile().getContainingDirectory().getVirtualFile().getCanonicalPath(); if (resolvedPath == null)
					return null;
				result += resolvedPath.replace(operand.getProject().getBasePath(), "") + "/";
				continue;
			}

			return null;
		}

		return result;
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		PsiElement resolved = resolve();
		StringLiteralExpression element = (StringLiteralExpression) getElement();
		if (resolved instanceof PsiFile) {
			/* Запускаем переименование */
			String resolvedFileName = ((PsiFile) resolved).getName();
			String elementContents = element.getContents();
				   elementContents = elementContents.substring(0, elementContents.lastIndexOf(resolvedFileName)) + newElementName;
			element.updateText(elementContents);
			/* Проверим, не потеряли ли ссылку на файл */
			checkComplicatedElement(element);
		}

		return element;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		StringLiteralExpression currentElement = (StringLiteralExpression) getElement();
		if (element instanceof PsiFile) {
			String elementPath = ((PsiFile) element).getVirtualFile().getPath();
			String projectPath = element.getProject().getBasePath();
			/* Обновляем содержимое текущего элемента */
			currentElement.updateText(elementPath.replace(projectPath, ""));
			/* Проверим, не потеряли ли ссылку на файл */
			checkComplicatedElement(currentElement);
		}

		return currentElement;
	}

	private void checkComplicatedElement(StringLiteralExpression element) {
		/* Исключительно в целях самоконтроля, в случае составной ссылки, проверим что связь с файлом не утеряна */
		if (element.getParent() instanceof BinaryExpression && resolve(element) == null) {
			String projectPath = element.getProject().getBasePath();
			String elementFile = element.getContainingFile().getVirtualFile().getCanonicalPath(); if (elementFile == null)
				elementFile = "Неизвестный файл";

			Notifications.Bus.notify(
				new Notification("Bitrix Framework Support", "Потенциальная проблема", "Перемещение файлов, ссылки на которых содержат конкатинацию, потенциально могут сработать неверно. Проверьте, всё ли хорошо тут " + elementFile.replace(projectPath, ""), NotificationType.ERROR)
			);
		}
	}
}
