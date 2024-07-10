package org.thymeleaf.engine;

import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IGatheringModelProcessable.class */
interface IGatheringModelProcessable extends IEngineProcessable {
    boolean isGatheringFinished();

    Model getInnerModel();

    void resetGatheredSkipFlags();

    ProcessorExecutionVars initializeProcessorExecutionVars();

    void gatherText(IText iText);

    void gatherComment(IComment iComment);

    void gatherCDATASection(ICDATASection iCDATASection);

    void gatherStandaloneElement(IStandaloneElementTag iStandaloneElementTag);

    void gatherOpenElement(IOpenElementTag iOpenElementTag);

    void gatherCloseElement(ICloseElementTag iCloseElementTag);

    void gatherUnmatchedCloseElement(ICloseElementTag iCloseElementTag);

    void gatherDocType(IDocType iDocType);

    void gatherXMLDeclaration(IXMLDeclaration iXMLDeclaration);

    void gatherProcessingInstruction(IProcessingInstruction iProcessingInstruction);
}