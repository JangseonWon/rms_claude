import type {Metadata} from "next";
import {Manrope} from 'next/font/google'
import "./globals.css";
import AuthSession from "@/app/_component/AuthSession";

const manrope = Manrope({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "RMS Plus",
  description: "rms.gcgenome.com",
};
type Props = {
  children: React.ReactNode
}
export default function RootLayout({children}:Props){
  return (
    <html lang="en">
      <body className={manrope.className}>
        <AuthSession>
          {children}
        </AuthSession>
      </body>
    </html>
  );
}
