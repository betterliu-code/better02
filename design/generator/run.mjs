import { chromium } from "playwright-core";
import { readFileSync, writeFileSync, existsSync } from "node:fs";
import { resolve, extname, join } from "node:path";
import { createServer } from "node:http";

const hostFile = process.argv[2] || "host.html";
const outPath = process.argv[3] || "out.fig";
const specPath = process.argv[4] || "spec.json";
const canvasName = process.argv[5] || "Library";

const root = resolve(".");
const spec = JSON.parse(readFileSync(specPath, "utf8"));

const MIME = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".ttf": "font/ttf",
  ".woff2": "font/woff2",
  ".css": "text/css; charset=utf-8",
  ".svg": "image/svg+xml",
  ".json": "application/json",
};

const server = createServer((req, res) => {
  const urlPath = decodeURIComponent(req.url.split("?")[0]);
  const filePath = join(root, urlPath === "/" ? "/index.html" : urlPath);
  if (!existsSync(filePath)) {
    res.writeHead(404);
    res.end("not found");
    return;
  }
  res.writeHead(200, { "content-type": MIME[extname(filePath)] || "application/octet-stream" });
  res.end(readFileSync(filePath));
});

await new Promise((r) => server.listen(0, r));
const port = server.address().port;
const url = `http://localhost:${port}/${hostFile}`;

const browser = await chromium.connectOverCDP("http://localhost:29229");
const ctx = browser.contexts()[0] || (await browser.newContext());
const page = await ctx.newPage();

page.on("console", (m) => console.log("[page]", m.type(), m.text()));
page.on("pageerror", (e) => console.log("[pageerror]", e.message));

await page.goto(url, { waitUntil: "load" });
await page.evaluate(async () => {
  await document.fonts.ready;
  await new Promise((r) => setTimeout(r, 500));
});

const base64 = await page.evaluate(
  async ([spec, canvasName]) => await window.__buildFig(spec, canvasName),
  [spec, canvasName]
);

const bytes = Buffer.from(base64, "base64");
writeFileSync(outPath, bytes);
console.log("wrote", outPath, bytes.length, "bytes");

await page.close();
await browser.close();
server.close();
