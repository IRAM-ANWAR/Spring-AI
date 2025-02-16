package com.spring.ai.config;

import java.io.File;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

//@Configuration
public class VectorLoader {

	@Value("classpath:/rag/constitution_of_india.pdf")
	private Resource pdfResource;

	@Bean
	SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
		SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
		File vectorStoreFile = new File("/Users/iram0/git/Spring-AI/src/main/resources/rag/vector_store.json");

		if (vectorStoreFile.exists()) {
			System.out.println("Loaded Vector Store File!");
			vectorStore.load(vectorStoreFile);
		} else {
			System.out.println("Creating Vector Store!");
			PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().withPagesPerDocument(1).build();
			PagePdfDocumentReader reader = new PagePdfDocumentReader(this.pdfResource, config);
			var textSplitter = new TokenTextSplitter();
			List<Document> docs = textSplitter.apply(reader.get());
			vectorStore.add(docs);
			vectorStore.save(vectorStoreFile);
			System.out.println("Vector Store Created Successfully");
		}
		return vectorStore;
	}
}