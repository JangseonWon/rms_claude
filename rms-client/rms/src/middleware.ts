import {auth} from "./auth"
import {NextResponse} from "next/server";

export async function middleware() {
  const session = await auth();
  if (!session) {
    return NextResponse.redirect('http://localhost:3000/login');
  }
}

// See "Matching Paths" below to learn more
export const config = {
  matcher: [
      '/dashboard',
      '/',
      '/request/:path*',
      '/home/:path*',
      '/user/:path*',
      '/users/:path*',
      '/dashboard/:path*',
      '/organizations/:path*',
      '/post/:path*',
      '/qna/:path*'
  ],
}