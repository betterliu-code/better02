import { createFigmaConverter } from "@figit/dom-to-figma";

const byteCache = new Map();
async function loadBytes(url) {
  if (!byteCache.has(url)) {
    byteCache.set(
      url,
      fetch(url).then((r) => {
        if (!r.ok) throw new Error("font fetch failed: " + url);
        return r.arrayBuffer();
      })
    );
  }
  return byteCache.get(url);
}

// All text uses Noto Sans SC / Noto Serif SC (full CJK+Latin glyph coverage,
// and both are available natively in Figma via Google Fonts).
const localFontLoader = async (req) => {
  const fam = (req.family || "").toLowerCase();
  const isSerif = fam.includes("serif");
  const url = isSerif ? "/fonts/NotoSerifSC.ttf" : "/fonts/NotoSansSC.ttf";
  const bytes = await loadBytes(url);
  return { bytes: bytes.slice(0), resolvedWeight: req.weight, resolvedItalic: false };
};

window.createFigmaConverter = createFigmaConverter;

window.__buildFig = async function buildFig(frameSpecs, canvasName) {
  const converter = createFigmaConverter({ fontLoader: localFontLoader });
  const frames = frameSpecs.map((sel) => {
    const element = document.querySelector(sel.selector);
    if (!element) throw new Error("missing element: " + sel.selector);
    return {
      element,
      width: sel.width,
      height: sel.height,
      x: sel.x,
      y: sel.y,
      name: sel.name,
    };
  });
  const result = await converter.convert({ frames, canvasName });
  return result.base64;
};
