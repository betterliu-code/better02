import { FigmaArchiveParser } from "fig-kiwi/dist/index.esm.js";
import { decodeBinarySchema, compileSchema } from "kiwi-schema";
import pako from "pako";
import { readFileSync } from "node:fs";

const path = process.argv[2] || "out.fig";
const buf = readFileSync(path);
const { header, files } = FigmaArchiveParser.parseArchive(new Uint8Array(buf));
const schema = decodeBinarySchema(pako.inflateRaw(files[0]));
const compiled = compileSchema(schema);
const message = compiled.decodeMessage(pako.inflateRaw(files[1]));

const changes = message.nodeChanges || [];
const byType = {};
for (const c of changes) byType[c.type] = (byType[c.type] || 0) + 1;
console.log("file:", path, "bytes:", buf.length, "header version:", header.version);
console.log("schema types:", schema.length, "| message type:", message.type);
console.log("nodeChanges:", changes.length, "| blobs:", (message.blobs || []).length);
console.log("by type:", JSON.stringify(byType));
const names = changes
  .filter((c) => ["FRAME", "CANVAS", "DOCUMENT"].includes(c.type))
  .map((c) => `${c.type}:${c.name}`);
console.log("frames/canvas:", JSON.stringify(names));
const texts = changes
  .filter((c) => c.type === "TEXT")
  .map((c) => c.textData?.characters)
  .filter(Boolean)
  .slice(0, 15);
console.log("text sample:", JSON.stringify(texts));
