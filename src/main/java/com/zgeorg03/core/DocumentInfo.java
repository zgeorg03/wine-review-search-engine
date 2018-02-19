package com.zgeorg03.core;

public class DocumentInfo {
    private final int documentId;
    private final String documentName;
    private final String content;

    public DocumentInfo(int documentId, String documentName, String content) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getDocumentName() {
        return documentName;
    }
}
