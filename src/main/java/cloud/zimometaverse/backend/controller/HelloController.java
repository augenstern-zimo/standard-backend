package cloud.zimometaverse.backend.controller;

import cloud.zimometaverse.backend.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public Result<String> hello() {
        return Result.success("hello world");
    }
}
