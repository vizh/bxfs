# Bitrix Framework Support #

Bitrix is popular in the former Soviet Union region, so there is no much sense to me and you to communicate in a foreign language. But you can feel free to write me in English anyway.

### Функционал: ###

* Поддержка папок bitrix и local.
* Переход на файл компонента, шаблона компонента (поддерживаются Twig и Smarty), шаблона сайта а так же на директории и файлы, найденные в строковых переменных.
* Переход на файл включаемой области при клике в вызове <?$APPLICATION→IncludeComponent('bitrix:main.include', ...);?> на значение ключа 'AREA_FILE_SUFFIX'.
* Корректное определение типов и автокомплит для [специальных переменных](http://dev.1c-bitrix.ru/api_help/main/general/magic_vars.php) и [переменных шаблона](http://dev.1c-bitrix.ru/learning/course/?COURSE_ID=43&LESSON_ID=2829#variables).
* Автокомплит компонента и его шаблона в процессе набора $APPLICATION→IncludeComponent(...) и CBitrixComponent::includeComponentClass(...)
* Решена проблема с подсветкой bitrix как неразрешённого пути в вызовах require($_SERVER["DOCUMENT_ROOT"]."/bitrix/header.php"); и похожих конструкциях.
* Безопасный рефакторинг файлов с автоматическим обновлением их вызовов в строковых переменных.
* Поиск использований, например файла some.css, в вызовах $APPLICATION→SetAdditionalCSS('...some.css'), во всех файлах проекта.
* Шаблоны создания типовых страниц и разделов сайта.

[Демо-видео](http://www.youtube.com/watch?v=37w7U65nVRU)

Пожелания и сообщения об ошибках можете отправлять на почту или оставлять в <a href="http://redmine.vizh.ru/projects/proj060/issues">Кабинете</a>. Там же можете [посмотреть планы](http://redmine.vizh.ru/projects/proj060/roadmap) по развитию.

### История версий ###

**0.1.7**
* Исправление ошибок.

**0.1.6**
* Автокомплит следующим суперглобальным переменным в .parameters.php: $componentName, $templateProperties, $arCurrentValues, $arComponentParameters и $componentPath;
* Автокомплит следующим суперглобальным переменным в component.php: $componentName, $componentTemplate, $parentComponentName, $parentComponentPath и $parentComponentTemplate.

**0.1.5**
* Автокомплит компонента, его шаблона и переход к ним в вызове CBitrixComponent::includeComponentClass(...).

**0.1.4**
* Несколько типовых шаблонов в диалоге создания страницы.

**0.1.3**
* Доступность переменных $arResult, $arParams, $componentPath и подобных им в файлах result_modifier.php.

**0.1.2**
* Создание типового раздела Битрикс сайта.

**0.1.1**
* Особая обработка путей в вызовах $APPLICATION→IncludeFile() в [соответствии с документацией Битрикс](http://dev.1c-bitrix.ru/api_help/main/reference/cmain/includefile.php);
* Создание типовой страницы Битрикс сайта.

**0.1**
* Ошибка определения ссылок на файлы в строках с конкатенацией;
* Автокомплит шаблона компонента в процессе набора $APPLICATION→IncludeComponent("bitrix:component", ...)

**0.0.9**
* Переход к коду компонента в class.php, если он есть и к component.php в противном случае;
* Поддержка нестандартных расширений для шаблонов компонентов: template.twig, template.tpl;
* Исправлена ошибка: BxPathReference has unsatisfied dependency.

**0.0.8**
* Переход на файлы header.php и footer.php шаблонов сайта;
* При поиске шаблонов компонента был опущен третий шаг [алгоритма поиска шаблона компонента](http://dev.1c-bitrix.ru/learning/course/index.php?COURSE_ID=43&LESSON_ID=2829#template_search).

**0.0.7**
* Корректное определение файлов в строках с конкатенацией, например в 'ind'.'ex.c'.'ss' или $APPLICATION→SetAdditionalCSS($APPLICATION→GetCurDir().'/some.css');
* Доступность переменных $arResult, $arParams и $componentPath в файлах component.php;
* Поддержка PhpStorm.

**0.0.6**
* Решена проблема с подсветкой bitrix в вызовах require($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/main/include.php");
* Поддержка безопасного удаления файлов с предупреждением о их использованиях в вызовах, например $APPLICATION→SetAdditionalCSS('/some.css');
* Поддержка переименования и перемещения файлов с автоматическим обновлением их вызовов, например $APPLICATION→SetAdditionalCSS('/some.css');
* Поиск использований, например файла some.css, в вызовах $APPLICATION→SetAdditionalCSS('...some.css'), во всех файлах проекта;
* Вышеперечисленную "магию" лучше посмотреть [наглядно](http://www.youtube.com/watch?v=37w7U65nVRU). В целом, тема интересная и есть куда развивать: автокомплит, например, создание отсутствующего файла и т.п.

**0.0.5**
* Решена проблема с подсветкой bitrix в вызовах require($_SERVER["DOCUMENT_ROOT"]."/bitrix/header.php");
* Автокомплит переменных, доступных в шаблоне компонента. Это $arResult, $arParams, $componentPath и другие, [описанные тут](http://dev.1c-bitrix.ru/learning/course/?COURSE_ID=43&LESSON_ID=2829#variables).

**0.0.4**
* Автокомплит компонента в процессе набора $APPLICATION→IncludeComponent(...)

**0.0.3**
* Переход на директории и файлы, найденные в строковых переменных.

**0.0.2**
* Корректное определение типов и автокомплит для [специальных переменных](http://dev.1c-bitrix.ru/api_help/main/general/magic_vars.php) $APPLICATION, $USER и $DB.

**0.0.1**
* Переход на шаблон компонента, расположенный внутри (bitrix|local)/templates/...
* Переход на файл включаемой области при клике в вызове <?$APPLICATION→IncludeComponent('bitrix:main.include', ...);?> на значение ключа 'AREA_FILE_SUFFIX';
* Поддержка папок bitrix и local.

