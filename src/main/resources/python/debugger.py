def get_object_attributes(obj):
    class Result:
        def is_empty(self):
            return not bool(self.__dict__)

    show_magic_methods = {show_magic_methods}
    show_methods = {show_methods}
    show_protected_fields = {show_protected_fields}
    show_private_fields = {show_private_fields}

    methods_ = Result()
    protected_fields = Result()
    private_fields = Result()
    dunder_methods = []
    cls = obj.__class__
    mro = cls.mro()

    def get_method_signature(method):
        """Получает сигнатуру метода с типами параметров и возвращаемого значения"""
        try:
            sig = inspect.signature(method)

            # Формируем строку с параметрами
            params = []
            for param_name, param in sig.parameters.items():
                param_str = param_name

                # Добавляем аннотацию типа параметра
                if param.annotation != inspect.Parameter.empty:
                    param_str += f": {_format_annotation(param.annotation)}"

                # Добавляем значение по умолчанию
                if param.default != inspect.Parameter.empty:
                    param_str += f" = {repr(param.default)}"

                params.append(param_str)

            # Формируем строку возвращаемого типа
            return_annotation = ""
            if sig.return_annotation != inspect.Signature.empty:
                return_annotation = f" -> {_format_annotation(sig.return_annotation)}"

            # Получаем имя метода
            method_name = getattr(method, "__name__", "unknown")

            return f"{method_name}({', '.join(params)}){return_annotation}"

        except Exception as e:
            # Если не удалось получить сигнатуру, возвращаем базовую информацию
            method_name = getattr(method, "__name__", "unknown")
            return f"{method_name}(...) [signature unavailable: {e}]"

    def _format_annotation(annotation):
        """Форматирует аннотацию типа в читаемый вид"""
        if hasattr(annotation, "__name__"):
            return annotation.__name__
        elif hasattr(annotation, "__module__") and hasattr(annotation, "__qualname__"):
            return f"{annotation.__module__}.{annotation.__qualname__}"
        else:
            return str(annotation)

    for name in dir(obj):
        try:
            if name.startswith("__") and name.endswith("__"):
                if show_magic_methods:
                    dunder_methods.append((name, get_method_signature(getattr(obj, name))))
                continue

            attr = getattr(obj, name)
        except Exception:
            continue

        # Является ли это метод (ручная проверка для исключения property)
        is_method = (
            inspect.ismethod(attr)
            or inspect.isfunction(attr)
            or inspect.isroutine(attr)
        )

        # Проверяем на mangled приватный атрибут для всех классов из MRO
        unmangled_name = None
        for base in mro:
            prefix = f"_{base.__name__}__"
            if name.startswith(prefix):
                unmangled_name = "__" + name[len(prefix) :]
                break

        # Определяем значение для сохранения
        if is_method:
            value = get_method_signature(attr)
        else:
            value = attr

        if unmangled_name:
            if is_method:
                setattr(methods_, f"{unmangled_name}().", value)
            else:
                setattr(private_fields, f"{unmangled_name}", value)
        elif name.startswith("_") and not name.startswith("__"):
            if is_method:
                setattr(methods_, f"{name}().", value)
            else:
                setattr(protected_fields, f"{name}", value)
        else:
            if is_method:
                setattr(methods_, f"{name}().", value)

    if show_methods:
        if not methods_.is_empty():
            setattr(obj, "methods", methods_)

    if show_protected_fields:
        if not protected_fields.is_empty():
            setattr(obj, "protected_fields", protected_fields)

    if show_private_fields:
        if not private_fields.is_empty():
            setattr(obj, "private_fields", private_fields)

    if show_magic_methods:
        if dunder_methods:
            setattr(obj, "dunder_methods", dunder_methods)

    return methods_
try:
    get_object_attributes({variable_name})
except Exception:
    pass
finally:
    'done'