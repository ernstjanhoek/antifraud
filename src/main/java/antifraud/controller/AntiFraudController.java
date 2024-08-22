package antifraud.controller;

import antifraud.feedback.FeedbackRequest;
import antifraud.feedback.FeedbackService;
import antifraud.ip.IpAddress;
import antifraud.ip.SuspiciousIpService;
import antifraud.stolencard.StolenCard;
import antifraud.stolencard.StolenCardService;
import antifraud.transaction.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    @Autowired
    TransactionService transactionService;

    @Autowired
    SuspiciousIpService ipService;

    @Autowired
    StolenCardService cardService;

    @Autowired
    FeedbackService feedbackService;

    @PostMapping("/suspicious-ip")
    public SuspiciousIpResponse registerSuspiciousIP(@RequestBody @Valid SuspiciousIpRequest request) {
        return SuspiciousIpResponse.fromIpAddress(ipService.addSuspiciousId(request.ip()));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public StatusResponse deregisterSuspiciousIP(@PathVariable
                                                 @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}")
                                                 String ip) {
        ipService.removeSuspiciousIp(ip);
        return new StatusResponse("IP %s successfully removed!".formatted(ip));
    }

    @GetMapping("/suspicious-ip")
    public List<SuspiciousIpResponse> getSuspiciousIPs() {
        return ipService.getSuspiciousIpAddresses().stream()
                .map(SuspiciousIpResponse::fromIpAddress)
                .toList();
    }

    @PostMapping("/stolencard")
    public StolenCardResponse registerStolenCard(@Valid @RequestBody StolenCardRequest request) {
        return StolenCardResponse.fromStolenCard(cardService.addStolenCard(request.number()));
    }

    @DeleteMapping("/stolencard/{number}")
    public StatusResponse deregisterStolenCard(@PathVariable @Valid @LuhnCheck String number) {
        cardService.removeStolenCard(number);
        return new StatusResponse("Card %s successfully removed!".formatted(number));
    }

    @GetMapping("/stolencard")
    public List<StolenCardResponse> getStolenCards() {
        return cardService.getAllStolenCards().stream()
                .map(StolenCardResponse::fromStolenCard)
                .toList();
    }

    @PostMapping("/transaction")
    public TransactionStatusWithInfo processTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.addTransaction(request);
    }

    @PutMapping("/transaction")
    public TxWithFeedbackResponse processFeedback(@Valid @RequestBody FeedbackRequest request) {
        return TxWithFeedbackResponse.fromTransaction(feedbackService.provideFeedback(request.transactionId(), request.feedback()));
    }

    @GetMapping("/history")
    public List<TxWithFeedbackResponse> getTransactionHistory() {
        return transactionService.getAllTransactions().stream()
                .map(TxWithFeedbackResponse::fromTransaction)
                .toList();
    }

    @GetMapping("/history/{number}")
    public  List<TxWithFeedbackResponse> getHistoryByCardNumber(@PathVariable @Valid @LuhnCheck String number) {
        return transactionService.getTransactionsByCardNumber(number).stream()
                .map(TxWithFeedbackResponse::fromTransaction)
                .toList();
    }

    public record StolenCardRequest(@NotEmpty @LuhnCheck String number) {}

    public record StolenCardResponse(long id, String number) {
        public static StolenCardResponse fromStolenCard(StolenCard card) {
            return new StolenCardResponse(card.getId(), card.getCardNumber());
        }
    }

    public record SuspiciousIpRequest(
            @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}")
            String ip) {}

    public record SuspiciousIpResponse(long id, String ip) {
        public static SuspiciousIpResponse fromIpAddress(IpAddress ip) {
            return new SuspiciousIpResponse(ip.getId(), ip.getIpAddress());
        }
    }
}
