import React, { useEffect, useRef } from 'react';

interface QrCodeViewProps {
  data: string;
  size?: number;
}

export const QrCodeView: React.FC<QrCodeViewProps> = ({ data, size = 200 }) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const matrixSize = 25;
    const matrix = generateQrMatrix(data, matrixSize);

    canvas.width = size;
    canvas.height = size;

    // Background
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, size, size);

    const cellSize = size / matrixSize;

    // Draw QR cells
    ctx.fillStyle = '#0f172a';
    for (let r = 0; r < matrixSize; r++) {
      for (let c = 0; c < matrixSize; c++) {
        if (matrix[r][c]) {
          ctx.fillRect(c * cellSize, r * cellSize, cellSize + 0.5, cellSize + 0.5);
        }
      }
    }

    // Draw Center UPI Badge
    const midX = size / 2;
    const midY = size / 2;
    const badgeRadius = size * 0.12;

    ctx.fillStyle = '#ffffff';
    ctx.beginPath();
    ctx.arc(midX, midY, badgeRadius + 2, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#e11d48';
    ctx.beginPath();
    ctx.arc(midX, midY, badgeRadius, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = '#ffffff';
    ctx.font = `bold ${Math.round(size * 0.12)}px sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('₹', midX, midY);
  }, [data, size]);

  return (
    <div className="flex flex-col items-center justify-center p-3 bg-white rounded-2xl border-2 border-slate-200 shadow-inner">
      <canvas ref={canvasRef} className="rounded-lg max-w-full" />
    </div>
  );
};

function generateQrMatrix(data: string, size: number): boolean[][] {
  const grid: boolean[][] = Array.from({ length: size }, () => Array(size).fill(false));

  const drawFinder = (startR: number, startC: number) => {
    for (let r = 0; r < 7; r++) {
      for (let c = 0; c < 7; c++) {
        const isBorder = r === 0 || r === 6 || c === 0 || c === 6;
        const isCenter = r >= 2 && r <= 4 && c >= 2 && c <= 4;
        grid[startR + r][startC + c] = isBorder || isCenter;
      }
    }
  };

  drawFinder(0, 0);
  drawFinder(0, size - 7);
  drawFinder(size - 7, 0);

  // Timing lines
  for (let i = 8; i < size - 8; i++) {
    grid[6][i] = i % 2 === 0;
    grid[i][6] = i % 2 === 0;
  }

  // Hash bits
  let hash = 0;
  for (let i = 0; i < data.length; i++) {
    hash = (hash << 5) - hash + data.charCodeAt(i);
    hash |= 0;
  }
  hash = Math.abs(hash);

  const mid = Math.floor(size / 2);
  for (let r = 0; r < size; r++) {
    for (let c = 0; c < size; c++) {
      const inFinder =
        (r < 8 && c < 8) ||
        (r < 8 && c >= size - 8) ||
        (r >= size - 8 && c < 8);
      if (inFinder || r === 6 || c === 6) continue;
      if (Math.abs(r - mid) <= 2 && Math.abs(c - mid) <= 2) {
        grid[r][c] = false;
        continue;
      }
      grid[r][c] = (r * 7 + c * 13 + hash) % 3 === 0;
    }
  }

  return grid;
}
