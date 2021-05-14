package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DrawerCreator {

    public static final int MAIN_ACTIVITY_DRAWER_ID = 1;
    public static final int SAVED_ARTICLES_DRAWER_ID = 2;
    public static final int SETTINGS_DRAWER_ID = 3;
    public static final int FEEDBACK_DRAWER_ID = 4;



    public static Drawer getDrawer(final Activity activity, Toolbar toolbar) {

        ImageHolder backgroundHeaderHolder = new ImageHolder(R.drawable.theconversation);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(backgroundHeaderHolder)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem drawerEmptyItem = new PrimaryDrawerItem().withIdentifier(0).withName("");
        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemHome = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.home).withIcon(R.drawable.ic_baseline_home_24);
        PrimaryDrawerItem drawerItemSavedArticles = new PrimaryDrawerItem().withIdentifier(2)
                .withName(R.string.savedArticlesActivity).withIcon(R.drawable.ic_baseline_article_24);


        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(3)
                .withName(R.string.settings).withIcon(R.drawable.ic_baseline_settings_24);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(4)
                .withName(R.string.about).withIcon(R.drawable.ic_baseline_info_24);
        SecondaryDrawerItem drawerItemHelp = new SecondaryDrawerItem().withIdentifier(5)
                .withName(R.string.help).withIcon(R.drawable.ic_baseline_help_24);
        SecondaryDrawerItem drawerItemDonate = new SecondaryDrawerItem().withIdentifier(6)
                .withName(R.string.donate).withIcon(R.drawable.ic_baseline_stars_24);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .withSliderBackgroundColor(activity.getResources().getColor(R.color.conv_red))
                .addDrawerItems(
                        drawerItemHome,
                        drawerItemSavedArticles,
                        new DividerDrawerItem(),
                        drawerItemHelp,
                        drawerItemSettings,
                        drawerItemAbout,
                        drawerItemDonate
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == SAVED_ARTICLES_DRAWER_ID && !(activity instanceof SavedArticlesActivity)) {
                            Intent move = new Intent(activity, SavedArticlesActivity.class);
                            view.getContext().startActivity(move);
                        }
                        else if (drawerItem.getIdentifier() == MAIN_ACTIVITY_DRAWER_ID && !(activity instanceof MainActivity)) {
                            Intent move = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(move);
                        }
                        else if (drawerItem.getIdentifier() == SETTINGS_DRAWER_ID && !(activity instanceof SettingsActivity)) {
                            Intent move = new Intent(activity, SettingsActivity.class);
                            view.getContext().startActivity(move);
                        }
                        else if (drawerItem.getIdentifier() == FEEDBACK_DRAWER_ID && !(activity instanceof askFeedbackActivity)) {
                            Intent move = new Intent(activity, askFeedbackActivity.class);
                            view.getContext().startActivity(move);
                        }
                        return true;
                    }
                })
                .build();

        return result;
    }
}
