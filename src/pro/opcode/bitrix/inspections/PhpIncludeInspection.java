package pro.opcode.bitrix.inspections;

import com.intellij.codeInsight.daemon.quickFix.CreateFileFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.Include;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/* toDo: Правильнее было бы вмешаться в процесс индексирования и добавить в индекс необходимые пути, пусть и виртуально.
 * Достаточно добавить файл в ResolveCache */
public class PhpIncludeInspection extends PhpInspection {
    public PhpIncludeInspection() {
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new PhpElementVisitor() {
            public void visitPhpInclude(Include include) {
                if(include.getStaticElement() != null && include.getError() == null) {
                    PsiReference[] var9 = include.getReferences();
                    int i = 0;

					/* Начало изменения */
                    /* toDo: Привязаться к BxTemplateReference.bxTemplateReferenceTargets */
					if (var9.length > 0 && Arrays.asList("bitrix", "local").contains(var9[0].getCanonicalText()))
						return;

//					if (var9.length == 2 && var9[0].getCanonicalText().equals("bitrix") && Arrays.asList("header.php", "footer.php").contains(var9[1].getCanonicalText()))
//						return;
//
//                    if (var9.length == 4 && var9[0].getCanonicalText().equals("bitrix") && var9[1].getCanonicalText().equals("modules") && var9[2].getCanonicalText().equals("main") && var9[3].getCanonicalText().equals("include.php"))
//                        return;
					/* Конец изменения */

                    for(int refsLength = var9.length; i < refsLength; ++i) {
                        PsiReference ref = var9[i];
                        if(ref instanceof FileReference && ref.resolve() == null) {
                            if(isOnTheFly) {
                                PsiDirectory dir;
                                try {
                                    if(i > 0) {
                                        PsiElement e = var9[i - 1].resolve();
                                        dir = e instanceof PsiDirectory?(PsiDirectory)e:null;
                                    } else {
                                        dir = include.getContainingFile().getParent();
                                    }
                                } catch (PsiInvalidElementAccessException var8) {
                                    throw new RuntimeException("Error while inspecting include reference: \'" + include.getText() + "\' at " + include.getContainingFile().getParent().getText(), var8);
                                }

                                holder.registerProblem(ref.getElement(), ref.getRangeInElement(), "Path \'" + ref.getCanonicalText() + "\' not found", dir != null?new LocalQuickFix[]{new CreateFileFix(i < var9.length - 1, ref.getCanonicalText(), dir)}:LocalQuickFix.EMPTY_ARRAY);
                            } else {
                                holder.registerProblem(include.getFirstChild(), "Can\'t resolve target of expression \'" + include.getFileName() + "\'", new LocalQuickFix[0]);
                            }

                            return;
                        }
                    }

                } else {
                    String refs = "Dynamic include expression \'#ref\' is not analysed.";
                    if(isOnTheFly && include.getError() != null) {
                        refs = refs + " " + include.getError();
                    }

                    holder.registerProblem(include, refs, ProblemHighlightType.WEAK_WARNING, new LocalQuickFix[0]);
                }
            }
        };
    }
}
