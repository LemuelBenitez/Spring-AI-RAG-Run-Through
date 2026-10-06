package com.project.spring_ai_rag;

import com.project.spring_ai_rag.rag.HRPolicyLoader;
import org.junit.jupiter.api.Test;
import com.project.spring_ai_rag.rag.RandomDataLoader;
import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.convention.TestBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreAutoConfiguration",
		"logging.level.root=INFO"
})
class SpringAiRagApplicationTests {

	@TestBean
	private VectorStore vectorStore;

	static VectorStore vectorStore() {
		return Mockito.mock(VectorStore.class);
	}

	// Keep startup data ingestion from calling Ollama and Elasticsearch in this context test.
	@TestBean
	private RandomDataLoader randomDataLoader;

	static RandomDataLoader randomDataLoader() {
		return new RandomDataLoader(null) {
			@Override
			public void loadSentenccesIntoVectorStore() {
				// This test checks context wiring without ingesting data.
			}
		};
	}

	@TestBean
	private HRPolicyLoader hrPolicyLoader;

	static HRPolicyLoader hrPolicyLoader() {
		return new HRPolicyLoader(null) {
			@Override
			public void loadPDF() {
				// This test checks context wiring without ingesting data.
			}
		};
	}

	@Autowired
	private EmbeddingModel embeddingModel;

	@Autowired
	private ChatModel chatModel;

	@Test
	void contextLoads() {
		assertThat(embeddingModel).isInstanceOf(OllamaEmbeddingModel.class);
		assertThat(chatModel).isInstanceOf(OllamaChatModel.class);
		assertThat(((OllamaChatModel) chatModel).getDefaultOptions().getModel()).isEqualTo("llama3.2:1b");
	}

}
