# Stream
Stuff that's stream related.

Find the stream [over here on Twitch](https://twitch.tv/livecodingwithsch3lp).

## TODOs

* [x] Also save the date at which a Quote was posted
* [x] Rename Either to Result/Ok/Error
* [x] Maybe rename Stub to Fake
* [x] And make sure they're ordered newest first
* [x] Make sharing quotes work
* [x] Improve QuoteShareProvider to return humanly readable shares
* [x] The share link is returned in a location header which is weird, don't do that
* [x] The share quote endpoint has the definition of a QuoteId twice: once in the url path and once in the payload/command. Either pick 1, or add validation that they need to be the same.
* [x] There's also a bunch of failure paths that haven't been tested in the repositories
* [x] Format localdatetime output in json properly (like so: "2024-05-07T21:53:00Z" and not "[2024,05,07,21,53,00]")
* [x] Figure out why the default objectmapper isn't used for conversion of objects to json when using RouterDsl. (It's because `@EnableWebMvc` expects you to configure and wire your own `ObjectMapper`)
* [x] Reading a SharedQuote (KQ-4.1)
* [x] Reading a SharedQuote returns the surrounding quotes (KQ-4.2)
* [ ] ~~Do pattern matching on the Accept header: if it's application/json return json, otherwise return html~~
* [ ] ResourceInitializationError: unable to pull secrets or registry auth: execution resource retrieval failed: unable to retrieve secrets from ssm: service call has been retried 5 time(s): RequestCanceled: request context canceled caused by: context deadline exceeded. Please check your task network configuration.


## Quotes

> Jonesuuu: When I kotlin, I kotlin. When I java, I java. But when I javascript.. I die a little on the inside.
> karel1980: "I don't like component scanning" should be the first quote you put in the system when it's live
> Amarth86: What is AWS doing when creating an RDS? Getting the bytes fresh from the amazon forest?
 
## Improvements for next time
### Before
* [x] Figure out royalty-free music again
* [x] Set up the Just Chatting scene
* [x] Set up the visual stream chat
* [ ] Set up a bot again to react to !socials, !discord, etc.
* [ ] Set up a bot again to block/ban spammers
* [ ] Do I want to save a local copy again to post to YouTube?
* [x] Make github repo public + add README.md and LICENSE

### Just Before/During
* [x] Configure StreamLabs' category to Creative 
* [x] Make sure to talk in the mic in one direction
* [x] Let the dog inside before starting the stream
