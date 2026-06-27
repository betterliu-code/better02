import { chromium } from "playwright-core";

const url = process.argv[2] || "http://localhost:8123/library.html";
const out = process.argv[3] || "shot.png";

const browser = await chromium.connectOverCDP("http://localhost:29229");
const ctx = browser.contexts()[0] || (await browser.newContext());
const page = await ctx.newPage();
await page.goto(url, { waitUntil: "load" });
await page.evaluate(async () => { await document.fonts.ready; await new Promise(r=>setTimeout(r,400)); });
await page.screenshot({ path: out, fullPage: true });
console.log("shot", out);
await page.close();
await browser.close();
