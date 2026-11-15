#!/data/data/com.termux/files/usr/bin/bash
# Noor-A-Zakat PRO v2.3.0 — production main-branch push
set -euo pipefail
pkg install -y git unzip curl python >/dev/null
termux-setup-storage >/dev/null 2>&1 || true
ZIP=""
for d in "$HOME/storage/downloads" "$HOME/storage/shared/Download"; do
  if [ -f "$d/NoorEZakat_v2.3.0_OnlineHijri_WidgetUpgrade.zip" ]; then ZIP="$d/NoorEZakat_v2.3.0_OnlineHijri_WidgetUpgrade.zip"; break; fi
done
if [ -z "$ZIP" ]; then echo "ZIP Downloads folder mein nahi mili: NoorEZakat_v2.3.0_OnlineHijri_WidgetUpgrade.zip"; exit 1; fi
GH_USER="automind786"
REPO="NoorEZakat"
BRANCH="main"
read -r -s -p "GitHub token (screen par nahi dikhega): " GH_TOKEN; echo
TMPROOT="$(mktemp -d "$HOME/.noor_push.XXXXXX")"
ASKPASS="$TMPROOT/askpass.sh"; RESPONSE="$TMPROOT/repo.json"; SRC="$TMPROOT/source"; WORK="$TMPROOT/work"
cleanup(){ unset GH_TOKEN GH_USER GIT_ASKPASS 2>/dev/null || true; rm -rf "$TMPROOT"; }
trap cleanup EXIT
cat > "$ASKPASS" <<'ASKPASS_EOF'
#!/data/data/com.termux/files/usr/bin/sh
case "$1" in *Username*) printf '%s\n' "$GH_USER" ;; *) printf '%s\n' "$GH_TOKEN" ;; esac
ASKPASS_EOF
chmod 700 "$ASKPASS"
export GH_USER GH_TOKEN GIT_ASKPASS="$ASKPASS" GIT_TERMINAL_PROMPT=0
HTTP_CODE="$(curl -sS -o "$RESPONSE" -w '%{http_code}' -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/vnd.github+json" "https://api.github.com/repos/$GH_USER/$REPO")"
if [ "$HTTP_CODE" != "200" ]; then echo "Repository access verify nahi hua (HTTP $HTTP_CODE)."; cat "$RESPONSE"; exit 1; fi
mkdir -p "$SRC"
unzip -q -o "$ZIP" -d "$SRC"
git clone "https://github.com/$GH_USER/$REPO.git" "$WORK"
cd "$WORK"
git fetch origin "$BRANCH"
git checkout "$BRANCH"
git reset --hard "origin/$BRANCH"
find "$WORK" -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +
cp -a "$SRC"/. "$WORK"/
git config user.name "$GH_USER"
git config user.email "$GH_USER@users.noreply.github.com"
git add -A
if git diff --cached --quiet; then echo "No changes detected. Nothing to push."; exit 0; fi
git commit -m "Fix Noor-A-Zakat v2.3.0 production layout UX and onboarding"
git push --force-with-lease origin "$BRANCH"
echo "Production push complete: https://github.com/$GH_USER/$REPO/tree/$BRANCH"
