from dataclasses import dataclass
from .const import (
    DetectMode,
    TemplateEnvironment,
    PythonEnvironment,
    ReplacedKeywordStrategy,
    AutoFix500Code,
)


@dataclass
class Options:
    """影响到攻击逻辑的选项"""

    detect_mode: DetectMode = DetectMode.ACCURATE
    environment: TemplateEnvironment = TemplateEnvironment.FLASK
    replaced_keyword_strategy: ReplacedKeywordStrategy = ReplacedKeywordStrategy.AVOID
    python_version: PythonEnvironment = PythonEnvironment.UNKNOWN
    autofix_500: AutoFix500Code = AutoFix500Code.ENABLED
