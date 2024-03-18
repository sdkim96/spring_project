// package web.web1.Config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.stereotype.Component;
// import web.web1.Map.domain.models.CafeTable;
// import web.web1.Map.domain.repository.CafeTableRepository;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;

// @Component
// public class CsvDataLoader implements CommandLineRunner {

//     @Autowired
//     private CafeTableRepository cafeTableRepository;

//     @Autowired
//     private ResourceLoader resourceLoader;

//     @Override
//     public void run(String... args) throws Exception {
//         BufferedReader reader = new BufferedReader(
//             new InputStreamReader(
//                 resourceLoader.getResource("classpath:CafeTable.csv").getInputStream(),
//                 StandardCharsets.UTF_8
//             )
//         );

//         String line;
//         // 첫 줄(헤더)을 읽고 버립니다.
//         reader.readLine();
//         // 데이터 처리를 시작합니다.
//         while ((line = reader.readLine()) != null) {
//             String[] data = line.split(",");
//             CafeTable cafeTable = CafeTable.builder()
//                 .cafeName(data[0])
//                 .province(data[1])
//                 .cityDistrict(data[2])
//                 .neighborhood(data[3])
//                 .longitude(Double.parseDouble(data[4]))
//                 .latitude(Double.parseDouble(data[5]))
//                 .build();
//             cafeTableRepository.save(cafeTable);
//         }
//     }

// }
