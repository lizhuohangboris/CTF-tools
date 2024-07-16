#!/usr/bin/env python

"""
Copyright (c) 2006-2024 sqlmap developers (https://sqlmap.org/)
See the file 'LICENSE' for copying permission
"""

from lib.core.common import unArrayizeValue
from lib.core.data import conf
from lib.core.data import kb
from lib.core.data import logger
from lib.core.data import queries
from lib.core.enums import DBMS
from lib.core.settings import H2_DEFAULT_SCHEMA
from lib.request import inject
from plugins.generic.enumeration import Enumeration as GenericEnumeration

class Enumeration(GenericEnumeration):
    def getBanner(self):
        if not conf.getBanner:
            return

        if kb.data.banner is None:
            infoMsg = "获取横幅信息"
            logger.info(infoMsg)

            query = queries[DBMS.H2].banner.query
            kb.data.banner = unArrayizeValue(inject.getValue(query, safeCharEncode=True))

        return kb.data.banner

    def getPrivileges(self, *args, **kwargs):
        warnMsg = "在 H2 上无法枚举用户权限"
        logger.warning(warnMsg)

        return {}

    def getHostname(self):
        warnMsg = "在 H2 上无法枚举主机名"
        logger.warning(warnMsg)

    def getCurrentDb(self):
        return H2_DEFAULT_SCHEMA

    def getPasswordHashes(self):
        warnMsg = "在 H2 上无法枚举密码哈希值"
        logger.warning(warnMsg)

        return {}

    def getStatements(self):
        warnMsg = "在 H2 上无法枚举 SQL 语句"
        logger.warning(warnMsg)

        return []