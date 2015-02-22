package pro.opcode.bitrix;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import pro.opcode.bitrix.references.*;

public class BxReferenceContributors extends PsiReferenceContributor
{	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		// provider for component name of $APPLICATION->IncludeComponent() call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentReference(), new PsiReferenceProvider() {
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentReference(element).getReference();
		}});

		// provider for component template name of $APPLICATION->IncludeComponent() call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentTemplateReference(), new PsiReferenceProvider(){
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentTemplateReference(element).getReference();
		}});

		// provider for include file of $APPLICATION->IncludeComponent('bitrix:main.include') call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentMainIncludeReference(), new PsiReferenceProvider(){
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentIncludeReference(element).getReference();
		}});

		/* Обработчик $APPLICATION->IncludeFile() */
		registrar.registerReferenceProvider(BxReferencePatterns.bxApplicationIncludeFileReference(), new PsiReferenceProvider() {
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext processingContext) {
			return new BxApplicationIncludeFileReference(element).getReference();
			}
		});

		/* Образботчик строковых переменных содержащих путь */
		registrar.registerReferenceProvider(BxReferencePatterns.bxPathReference(), new PsiReferenceProvider(){
				@NotNull @Override
				public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxPathReference(element).getReference();
		}});

		/* Обработчик подключений bitrix/header.php и bitrix/footer.php */
		registrar.registerReferenceProvider(BxReferencePatterns.bxTemplateReference(), new PsiReferenceProvider(){
				@NotNull @Override
				public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxTemplateReference(element).getReference();
		}});
	}
}
