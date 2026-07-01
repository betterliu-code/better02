#!/usr/bin/env bash
# 下载用于排版度量的 Noto 字体 (变量字体, 含完整中文 + 拉丁字形)。
# .fig 中只内嵌字体度量, Figma 打开时用其自带的 Google Fonts 渲染,
# 所以这两个字体只在"生成阶段"需要, 不会提交进仓库。
set -euo pipefail
mkdir -p fonts
echo "下载 Noto Sans SC ..."
curl -sL "https://raw.githubusercontent.com/google/fonts/main/ofl/notosanssc/NotoSansSC%5Bwght%5D.ttf" -o fonts/NotoSansSC.ttf
echo "下载 Noto Serif SC ..."
curl -sL "https://raw.githubusercontent.com/google/fonts/main/ofl/notoserifsc/NotoSerifSC%5Bwght%5D.ttf" -o fonts/NotoSerifSC.ttf
ls -la fonts
