import {auth} from "./auth"
import {NextResponse} from "next/server";

export async function middleware() {
  const session = await auth();
  if (!session) {
    return NextResponse.redirect(`${process.env.BASE_URL}/login`);
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
      '/service-catalog/:path*',
      '/post/:path*',
      '/qna/:path*'
  ],
}